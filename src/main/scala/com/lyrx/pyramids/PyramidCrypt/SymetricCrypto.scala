package com.lyrx.pyramids.PyramidCrypt

import org.scalajs.dom.crypto.{AlgorithmIdentifier, CryptoKey, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, crypto}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.concurrent.ExecutionContext
import scala.scalajs.js.typedarray.ArrayBufferView

trait SymetricCrypto extends Crypto {
  val ALGORITHM = "AES-GCM"


  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={
    l( "name" -> ALGORITHM,
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }
  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier= l(
    "name" -> ALGORITHM,
    "length" -> 256).asInstanceOf[KeyAlgorithmIdentifier]


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
