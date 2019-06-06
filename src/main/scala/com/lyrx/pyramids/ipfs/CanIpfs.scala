package com.lyrx.pyramids.ipfs


import com.lyrx.pyramids.pcrypto
import com.lyrx.pyramids.pcrypto.WalletNative
import pcrypto.PCryptoImplicits._
import com.lyrx.pyramids.{Pyramid, PyramidConfig, PyramidJSON}
import org.scalajs.dom.raw.{File, FileReader}
import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.scalajs.js.JSON
import scala.scalajs.js.typedarray.Uint8Array
//import typings.nodeLib.bufferMod.Buffer

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dynamic.{literal => l}

trait CanIpfs extends pcrypto.Crypto with PyramidJSON {
  val pyramidConfig: PyramidConfig

  def initIpfs()(implicit executionContext: ExecutionContext) = Future {
    new Pyramid(
      pyramidConfig
        .copy(
          ipfsOpt = Some(
            IpfsHttpClient(
              l(
                "host" -> "ipfs.infura.io",
                "port" -> 5001,
                "protocol" -> "https"
              )))
        )
        .msg("Connected to IPFS network!"))
  }

  def initIpfsAndPublishPublicKeys()(
      implicit executionContext: ExecutionContext) =
    initIpfs().flatMap(_.publicKeysToIpfs())

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
          pyramidConfig.ipfsOpt
            .map(ipfs => ipfs.futureAdd(n).map(Some(_)))
            .getOrElse(Future { None }))
      .map(_.flatMap(_.headOption.map(_.hash)))
      .map(_.map(s =>
        new Pyramid(pyramidConfig.
          withPubKeys(s).
          msg(s"Oh Pharao, we have published your divine signature!")))
        .getOrElse(new Pyramid(pyramidConfig)))

  def bufferToIpfs(buffer: nodeLib.Buffer)(implicit ctx: ExecutionContext) =
    pyramidConfig.ipfsOpt
      .map(_.futureAdd(buffer).map(l => Some(l.head.hash)))
      .getOrElse(Future { None })

  def readIpfs(aHash:String)
              (implicit executionContext: ExecutionContext)
  = pyramidConfig.
    ipfsOpt.map(ipfsClient => ipfsClient.
    futureCat(aHash).map(Some(_))).
    getOrElse(Future{None})


  def readIpfsString(aHash:String)
              (implicit executionContext: ExecutionContext)
  =   pyramidConfig.
    ipfsOpt.map(ipfsClient => ipfsClient.
    futureCatString(aHash).map(Some(_))).
    getOrElse(Future{None})







  def readIpfsWallet()
                    (implicit executionContext: ExecutionContext)
  = readIpfsString(pyramidConfig.ipfsData.pharao).
    map(_.map(s=>JSON.parse(s)).asInstanceOf[WalletNative]).
    map(wallet=>pyramidConfig.msg(s"Oh Pharao, we have read your divine Wallet ..."))




}
