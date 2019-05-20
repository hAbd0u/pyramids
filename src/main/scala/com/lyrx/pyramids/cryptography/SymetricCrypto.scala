package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.DistributedData
import org.scalajs.dom.crypto.{AlgorithmIdentifier, CryptoKey, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, crypto}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}
import js.Dynamic.{literal => l}
trait SymetricCrypto extends Crypto {
  val ALGORITHM = "AES-GCM"


  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={
    l( "name" -> ALGORITHM,
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }
  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier= l(
    "name" -> ALGORITHM,
    "length" -> 256).asInstanceOf[KeyAlgorithmIdentifier]



  def encrypt(symKey:CryptoKey,distributedData: DistributedData)(implicit ctx:ExecutionContext)=
    distributedData.unencryptedOpt.
      map(arrayBuffer => {
        val iv = crypto.getRandomValues(new Uint8Array(12))
        crypto.
          subtle.
          encrypt(algorithmIdentifier(iv),
            symKey,
            arrayBuffer
          ).
          toFuture.
          map(_.asInstanceOf[ArrayBuffer]).map(b=>distributedData.copy(
          bufferOpt = Some(b),
          ivOpt = Some(iv.buffer)))
      }).getOrElse(Future{distributedData})







  def generateSymmetricKey()(implicit ctx:ExecutionContext)= crypto.
    subtle.
    generateKey(
      keyAlgorithmIdentifier,
      true,
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
    true,
    js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)).
    toFuture.
    map(_.asInstanceOf[CryptoKey])



}
