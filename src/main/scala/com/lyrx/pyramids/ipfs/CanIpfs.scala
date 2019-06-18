package com.lyrx.pyramids.ipfs


import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, WalletNative}
import com.lyrx.pyramids.{InfuraIpfsImpl, Pyramid, PyramidConfig, PyramidJSON, pcrypto}
import org.scalajs.dom.crypto.CryptoKey
import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.scalajs.js.JSON
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dynamic.{literal => l}

trait CanIpfs extends pcrypto.Crypto with PyramidJSON with AsymetricCrypto {
  val pyramidConfig: PyramidConfig

  def initIpfs()(implicit executionContext: ExecutionContext):Future[Pyramid]



  def initIpfsAndPublishPublicKeys()(
      implicit executionContext: ExecutionContext) :Future[Pyramid]

  def exportAllPublicKeys()(
      implicit ctx: ExecutionContext): Future[pcrypto.CryptoTypes.JsonWebKeyOptPair] =
    exportPublicKey(pyramidConfig.asymKeyOpt).flatMap(pubKeyOpt =>
      exportPublicKey(pyramidConfig.signKeyOpt).map(signKeyOpt =>
        (pubKeyOpt, signKeyOpt)))

  def publicKeysToBuffer()(implicit ctx: ExecutionContext) =
    exportAllPublicKeys()
      .map(
        kp =>
          l(
            "asym" -> l("public" -> kp._1.getOrElse(null)),
            "sign" -> l("public" -> kp._2.getOrElse(null))
          ).asInstanceOf[pcrypto.WalletNative])
      .map(
        w => bufferMod.Buffer.from(stringify(w))
      )

  def publicKeysToIpfs()(implicit ctx: ExecutionContext) =
    publicKeysToBuffer()
      .flatMap(
        n =>
          pyramidConfig.infuraClientOpt
            .map(ipfs => ipfs.futureAdd(n).map(Some(_)))
            .getOrElse(Future { None }))
      .map(_.flatMap(_.headOption.map(_.hash)))
      .map(_.map(s =>
        new Pyramid(pyramidConfig.
          withPubKeys(s).
          msg(s"Oh Pharao, we have published your divine signature!")))
        .getOrElse(new Pyramid(pyramidConfig)))

  def bufferToIpfs(buffer: nodeLib.Buffer)(implicit ctx: ExecutionContext) =
    pyramidConfig.infuraClientOpt
      .map(_.futureAdd(buffer).map(l => Some(l.head.hash)))
      .getOrElse(Future { None })

  def readIpfs(aHash:String)
              (implicit executionContext: ExecutionContext)
  = pyramidConfig.
    infuraClientOpt.map(ipfsClient => ipfsClient.
    futureCat(aHash).map(Some(_))).
    getOrElse(Future{None})





  def readIpfsString(aHash:String)
              (implicit executionContext: ExecutionContext)
  =   pyramidConfig.
    infuraClientOpt.map(ipfsClient => ipfsClient.
    futureCatString(aHash).map(Some(_))).
    getOrElse(Future{None})



  def readIpfsNativeWallet()
                    (implicit executionContext: ExecutionContext)
  = readIpfsString(pyramidConfig.ipfsData.pharaoData.pubkey).
    map(_.map(s=>JSON.parse(s).asInstanceOf[WalletNative]))



  def readPharaoKeys()
                      (implicit executionContext: ExecutionContext)
  = readIpfsNativeWallet().flatMap(_.flatMap(nativeWallet=>
    nativeWallet.`asym`.toOption.flatMap(_.`public`.toOption.map(k =>
      importKey(
        jsonWebKey = k ,
        usages = usageEncrypt,
        aHashAlgorithm)
    ))).getOrElse(Future{None}))



  def readPharaoWallet()
                    (implicit executionContext: ExecutionContext)
  = readPharaoKeys().map( (keyOpt:Option[CryptoKey])=>
    keyOpt.map(key=>pyramidConfig.msg(
      s"Oh Pharao, here are your devine public keys!")).
    getOrElse(pyramidConfig.msg("Oh Pharao, please excuse! We did not find your keys!"))
    )



}
