package com.lyrx.pyramids.pcrypto

import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey, KeyFormat, crypto}
import org.scalajs.dom.raw.File
import com.lyrx.pyramids.ipfs.{IPFSMetaData, TextDecoder}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.typedarray.{ArrayBuffer, Uint8Array}

object EncryptedData{
  def apply():EncryptedData=EncryptedData(
    None,None,None,None,None,None,None
  )
}


case class DecryptedData(unencrypted: Option[ArrayBuffer],
                         metaData:Option[IPFSMetaData]
                        ){
  def isEmpty() =(
    unencrypted.isEmpty  &&
    metaData.isEmpty)

  def descr()=if(isEmpty()) "no decrypted data" else "decrypted data"

}

case class EncryptedData(unencrypted: Option[ArrayBuffer],
                         encrypted:Option[ArrayBuffer],
                         random:Option[ArrayBuffer],
                         signature:Option[ArrayBuffer],
                         metaData:Option[ArrayBuffer],
                         metaRandom:Option[ArrayBuffer],
                         signer:Option[ArrayBuffer]
                        )
  extends Encrypted




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
