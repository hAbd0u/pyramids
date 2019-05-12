package com.lyrx.pyramids.PyramidCrypt

import org.scalajs.dom.crypto.{CryptoKeyPair, HashAlgorithm, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, RsaHashedKeyAlgorithm, crypto}

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array
import js.Dynamic.{literal => l}
import scala.concurrent.{ExecutionContext, Future}
trait AsymetricCrypto {
  val aKeyFormat =  KeyFormat.jwk
  val aHashAlgorithm =  RsaHashedKeyAlgorithm.`RSA-OAEP`(modulusLength = 4096,
    publicExponent = new Uint8Array( js.Array(1,0,1)),
    hash = HashAlgorithm.`SHA-256`)

  val aSignAlgorithm = l(
    "name" ->"ECDSA",
    "namedCurve" -> "P-384"
  ).asInstanceOf[KeyAlgorithmIdentifier]

  val aAlgorithm:KeyAlgorithmIdentifier = js.Dynamic.
    literal("name" -> "RSA-OAEP").
    asInstanceOf[KeyAlgorithmIdentifier]




  def generateASymetricEncryptionKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = generateKeysFor( js.Array(KeyUsage.encrypt, KeyUsage.decrypt),aHashAlgorithm)


  def generateSignKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = generateKeysFor(js.Array(KeyUsage.sign, KeyUsage.verify), aSignAlgorithm)




  private def generateKeysFor(aUsage:js.Array[KeyUsage],alg:KeyAlgorithmIdentifier)
                     (implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = crypto.subtle.generateKey(
    algorithm = alg
    ,
    extractable = true,
    keyUsages = aUsage).
    toFuture.map(_.asInstanceOf[CryptoKeyPair])


}
