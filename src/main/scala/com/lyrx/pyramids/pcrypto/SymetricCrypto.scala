package com.lyrx.pyramids.pcrypto

import com.lyrx.pyramids.PyramidJSON
import com.lyrx.pyramids.pcrypto.PCryptoImplicits._
import org.scalajs.dom.crypto.{AlgorithmIdentifier, CryptoKey, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, crypto}
import org.scalajs.dom.raw.{File, FileReader}
import typings.nodeLib.bufferMod.Buffer

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}



trait SymetricCrypto extends Crypto with PyramidJSON {
  val ALGORITHM = "AES-GCM"


  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={
    l( "name" -> ALGORITHM,
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }
  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier= l(
    "name" -> ALGORITHM,
    "length" -> 256).asInstanceOf[KeyAlgorithmIdentifier]






  def metaDataFrom(f: File) = Buffer.
    from(stringify(
      l("name"  -> f.name,
        "type" -> f.`type`,
        "size"  -> f.size
      ))).
    buffer.asInstanceOf[ArrayBuffer]

  def encryptArrayBuffer(key:CryptoKey,b:ArrayBuffer)
                        (implicit executionContext: ExecutionContext)={
    val iv = crypto.getRandomValues(new Uint8Array(12))
    crypto.
      subtle.
      encrypt(algorithmIdentifier(iv), key, b).
      toFuture.map(_.asInstanceOf[ArrayBuffer]).
      map(b2 => (b2,iv.buffer))
  }


  def decryptArrayBuffer(key:CryptoKey,data:ArrayBuffer,iv:ArrayBuffer)
                        (implicit executionContext: ExecutionContext)=
    crypto.
      subtle.
      decrypt(algorithmIdentifier(new Uint8Array(iv)), key, data).
      toFuture.map(_.asInstanceOf[ArrayBuffer])








  def symEncryptFile(key:CryptoKey, f:File)
                    (implicit executionContext: ExecutionContext) =
    new FileReader().
    futureReadArrayBuffer(f).
    flatMap(encryptFileData(key, _)).
      flatMap(encryptMetaData(_, key, f))


  def encryptFileData(key: CryptoKey, unencryptedData: ArrayBuffer)
                     (implicit executionContext: ExecutionContext) = {

    val iv = crypto.getRandomValues(new Uint8Array(12))
    crypto.
      subtle.
      encrypt(algorithmIdentifier(iv),
        key,
        unencryptedData
      ).
      toFuture.map(r => EncryptedData(
      Some(unencryptedData),
      Some(r.asInstanceOf[ArrayBuffer]),
      Some(iv.buffer),
      None,
      None,
      None,
      None
    ))

  }

  def encryptMetaData(e:EncryptedData,
                      key: CryptoKey, f: File)
                     (implicit executionContext: ExecutionContext) =
    encryptArrayBuffer(
      key,
      metaDataFrom(f)).
      map(r =>
        e.copy(
          metaData = Some(r._1),
          metaRandom = Some(r._2)
        ))


  def generateSymmetricKey()(implicit ctx:ExecutionContext)= crypto.
    subtle.
    generateKey(
      keyAlgorithmIdentifier,
      extractable = true,
      js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)).
    toFuture.
    map(_.asInstanceOf[CryptoKey])

  def importSymetricKey(jsonWebKey: JsonWebKey)(
    implicit executionContext: ExecutionContext)=crypto.subtle.importKey(
    KeyFormat.jwk,
    jsonWebKey,
    keyAlgorithmIdentifier,
    extractable = true,
    js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)).
    toFuture.
    map(_.asInstanceOf[CryptoKey])



}
