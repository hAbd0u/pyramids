package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.pcrypto.AsymetricCrypto
import com.lyrx.pyramids.{Pyramid, PyramidConfig, pcrypto}
import org.scalajs.dom.crypto.CryptoKey
import typings.nodeLib
import typings.nodeLib.bufferMod

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dynamic.{literal => l}





trait EncryptedIpfs  extends AsymetricCrypto with CanIpfs {

  val pyramidConfig: PyramidConfig

  def exportAllPublicKeys()(implicit ctx: ExecutionContext)
  : Future[pcrypto.CryptoTypes.JsonWebKeyOptPair] =
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
        new Pyramid(pyramidConfig
          .withPubKeys(s)
          .msg(s"Oh ${pyramidConfig.name()}, we have published your divine signature!")))
        .getOrElse(new Pyramid(pyramidConfig)))


  def savePubKeyEncryptedStringToIpfs(s: String)(
    implicit ctx: ExecutionContext) = {
    //println(s"Saving '${s}'")
    pyramidConfig.asymKeyOpt
      .map(k => saveArrayBufferToIpfs(asymEncryptString(k.publicKey, s)))
      .getOrElse(Future { None })
  }

  def readPharaoKeys()(implicit executionContext: ExecutionContext) =
    readIpfsNativeWallet().flatMap(
      _.flatMap(nativeWallet =>
        nativeWallet.`asym`.toOption.flatMap(_.`public`.toOption.map(k =>
          importKey(jsonWebKey = k, usages = usageEncrypt, aHashAlgorithm))))
        .getOrElse(Future { None }))

  def readPharaoWallet()(implicit executionContext: ExecutionContext) =
    readPharaoKeys().map(
      (keyOpt: Option[CryptoKey]) =>
        keyOpt.map(key =>
          pyramidConfig.msg(
            s"Oh ${pyramidConfig.name()}, here are your public keys!")))


  def readAndDecrypt(aHash: String)(
    implicit executionContext: ExecutionContext) =
    pyramidConfig.infuraClientOpt
      .map(
        ipfsClient =>
          ipfsClient
            .futureCat(aHash)
            .flatMap(((b: nodeLib.Buffer) => {
              pyramidConfig.asymKeyOpt
                .map(k =>
                  asymDecryptBuffer(k.privateKey, b).map(b =>
                    Some(b.myToString())))
                .getOrElse(Future { None })
            })))
      .getOrElse(Future { None })

}
