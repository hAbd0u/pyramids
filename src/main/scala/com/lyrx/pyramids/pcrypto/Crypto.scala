package com.lyrx.pyramids.pcrypto

import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey, KeyFormat, crypto}

import scala.concurrent.{ExecutionContext, Future}





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
