package com.lyrx.pyramids.PyramidCrypt

import org.scalajs.dom.crypto.{AlgorithmIdentifier, CryptoKey, KeyAlgorithmIdentifier, KeyUsage, crypto}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.concurrent.ExecutionContext
import scala.scalajs.js.typedarray.ArrayBufferView

trait SymmetricCrypto {
  val ALGORITHM = "AES-GCM"


  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={
    l( "name" -> ALGORITHM,
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }
  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier= l(
    "name" -> ALGORITHM).asInstanceOf[KeyAlgorithmIdentifier]


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


}
