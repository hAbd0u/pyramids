package com.eternitas.lastwill.cryptoo

import org.scalajs.dom.crypto.{AesGcmParams, Algorithm, CryptoKey, KeyAlgorithmIdentifier, KeyUsage, crypto}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.concurrent.ExecutionContext

object SymCrypto extends SymCryptoTrait {
  override val keyAlgorithmIdentifier:KeyAlgorithmIdentifier =l(

    "name" -> "AES-GCM",
    "length" -> 256
  ).asInstanceOf[KeyAlgorithmIdentifier]

}



trait SymCryptoTrait {


  val encryptDecrypt= js.Array(
    KeyUsage.encrypt,
    KeyUsage.decrypt)

  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier

  def generateKey()(implicit ctx:ExecutionContext)= crypto.
    subtle.
    generateKey(keyAlgorithmIdentifier,true, encryptDecrypt).
    toFuture.
    map(_.asInstanceOf[CryptoKey])



}
