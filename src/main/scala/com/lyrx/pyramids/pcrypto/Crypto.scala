package com.lyrx.pyramids.pcrypto

import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, JsonWebKey, KeyFormat, crypto}
import org.scalajs.dom.raw.File
import com.lyrx.pyramids.ipfs.{IPFSMetaData, TextDecoder}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.typedarray.{ArrayBuffer, Uint8Array}

trait Encrypted extends SymetricCrypto {
  val unencrypted: Option[ArrayBuffer]
  val encrypted: Option[ArrayBuffer]
  val random: Option[ArrayBuffer]
  val signature: Option[ArrayBuffer]
  val metaData:Option[ArrayBuffer]
  val metaRandom:Option[ArrayBuffer]
  val signer:Option[ArrayBuffer]


  def decryptData(symKey:CryptoKey)(implicit executionContext: ExecutionContext) =
    encrypted.
    map(data=>decryptArrayBuffer(
      symKey,
      data,
      random.get
    ).map(b=>DecryptedData(Some(b),None))).
      getOrElse(
        Future{DecryptedData(None,None)
        })


  def decrypt(symKey:CryptoKey)(implicit executionContext: ExecutionContext) =
    decryptData(symKey).flatMap(d=>
      metaData.
        map(data=>decryptArrayBuffer(
          symKey,
          data,
          metaRandom.get
        ).map(b=>DecryptedData(
            d.unencrypted,
            Some(js.JSON.parse(
              new TextDecoder().
              decode(
                new Uint8Array(b))).asInstanceOf[IPFSMetaData])
        ))).
        getOrElse(
          Future{DecryptedData(d.unencrypted,None)
          }))





  def isEmpty() =(
    unencrypted.isEmpty &&
    encrypted.isEmpty &&
    random.isEmpty &&
    signature.isEmpty &&
    metaData.isEmpty &&
    metaRandom.isEmpty &&
    signer.isEmpty
  )

  def descr()=if(isEmpty()) "no encrypted data" else "encrypted data"
}

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
