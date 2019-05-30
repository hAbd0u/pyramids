package com.lyrx.pyramids.pcrypto

import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey, KeyFormat, crypto}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait Encrypted {
  val unencrypted: Option[ArrayBuffer]
  val encrypted: Option[ArrayBuffer]
  val random: Option[ArrayBuffer]
  val signature: Option[ArrayBuffer]
}

case class EncryptedData(unencrypted: Option[ArrayBuffer],
                         encrypted:Option[ArrayBuffer],
                         random:Option[ArrayBuffer],
                         signature:Option[ArrayBuffer])
  extends Encrypted{

}


object CryptoTypes {
  type PyramidCryptoKey = CryptoKey
  type PyramidCryptoKeyPair = CryptoKeyPair
  type JsonKeyPair = (JsonWebKey, JsonWebKey)
  type JSKeyPairOpt = Option[JsonKeyPair]
  type JSKeyOpt = Option[JsonWebKey]
  type AllJSKeysOpt = (JSKeyOpt, JSKeyPairOpt, JSKeyPairOpt)
  type JsonWebKeyOptPair = (Option[JsonWebKey], Option[JsonWebKey])

  type EncryptionResult = Encrypted

}

trait Crypto {

  def exportCryptoKey(key: CryptoKey)(implicit ctx: ExecutionContext) =
    crypto.subtle
      .exportKey(KeyFormat.jwk, key)
      .toFuture
      .map((a: Any) => a.asInstanceOf[JsonWebKey])

  def exportKeys(keyPairOpt: Option[CryptoKeyPair])(
      implicit ctx: ExecutionContext) =
    keyPairOpt
      .map(
        keys =>
          exportCryptoKey(keys.publicKey).flatMap(
            pubKeyJsonWeb =>
              exportCryptoKey(
                keys.privateKey
              ).flatMap(privKeyJsonWeb =>
                Future { Some((privKeyJsonWeb, pubKeyJsonWeb)) })))
      .getOrElse(Future { None })

  def exportPublicKey(keyPairOpt: Option[CryptoKeyPair])(
      implicit ctx: ExecutionContext) =
    keyPairOpt
      .map(keys =>
        exportCryptoKey(keys.publicKey).map((k: JsonWebKey) => Some(k)))
      .getOrElse(Future { None })
  //.getOrElse(Future{None})

}
