package com.lyrx.pyramids.PyramidCrypt

import org.scalajs.dom.crypto.{AlgorithmIdentifier, KeyAlgorithmIdentifier}

import scala.scalajs.js

import js.Dynamic.{literal => l}
import scala.scalajs.js.typedarray.ArrayBufferView

trait SymmetricCrypto {
  val ALGORITHM = "AES-GCM"


  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={
    l( "name" -> ALGORITHM,
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }
  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier= l(
    "name" -> ALGORITHM).asInstanceOf[KeyAlgorithmIdentifier]



}
