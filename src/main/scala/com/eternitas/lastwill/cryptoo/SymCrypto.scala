package com.eternitas.lastwill.cryptoo


import org.scalajs.dom.crypto.{AesGcmParams, Algorithm, AlgorithmIdentifier, CryptoKey, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, crypto}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}

object SymCrypto extends SymCryptoTrait {

  override val aKeyFormat =  KeyFormat.jwk
  override val keyAlgorithmIdentifier:KeyAlgorithmIdentifier =l(

    "name" -> "AES-GCM",
    "length" -> 256
  ).asInstanceOf[KeyAlgorithmIdentifier]

}


case class SymEncryptionResult(result:ArrayBuffer,iv: ArrayBufferView)




trait SymCryptoTrait {


  val aKeyFormat:KeyFormat
  val encryptDecrypt= js.Array(
    KeyUsage.encrypt,
    KeyUsage.decrypt)

  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier
  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={

    l( "name" -> "AES-GCM",
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }

  def generateKey()(implicit ctx:ExecutionContext)= crypto.
    subtle.
    generateKey(keyAlgorithmIdentifier,true, encryptDecrypt).
    toFuture.
    map(_.asInstanceOf[CryptoKey])


  def encrypt(key:CryptoKey,data:ArrayBuffer)
             (implicit executionContext: ExecutionContext):Future[SymEncryptionResult]= {
    val iv:ArrayBufferView = crypto.getRandomValues(new Uint8Array(12))
    crypto.
      subtle.
      encrypt(algorithmIdentifier(iv),
        key,
        data
      ).toFuture.
      map(aAny=>SymEncryptionResult(aAny.asInstanceOf[ArrayBuffer],iv))
  }

  def decrypt(key:CryptoKey,data:ArrayBuffer,iv:ArrayBuffer)
             (implicit executionContext: ExecutionContext):Future[ArrayBuffer]= {
    crypto.
      subtle.
      decrypt(algorithmIdentifier(new Uint8Array(iv,0,12)),
        key,
        data
      ).toFuture.
      map(aAny=>aAny.asInstanceOf[ArrayBuffer])
  }






  def eexportKey(key: CryptoKey)
                (implicit ctx:ExecutionContext) = crypto.subtle.
    exportKey(aKeyFormat,key).
    toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])



}
