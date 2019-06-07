package com.lyrx.pyramids.pcrypto

import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, HashAlgorithm, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, RsaHashedKeyAlgorithm, crypto}
import org.scalajs.dom.raw.{File, FileReader}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import PCryptoImplicits._
import com.lyrx.pyramids.PyramidJSON
import com.lyrx.pyramids.ipfs.{TextDecoder, TextEncoder}
import typings.nodeLib.Buffer

import js.Dynamic.{literal => l}
import scala.io.BufferedSource
trait AsymetricCrypto extends Crypto with PyramidJSON{

  val aHashAlgorithm:KeyAlgorithmIdentifier =  RsaHashedKeyAlgorithm.`RSA-OAEP`(modulusLength = 4096,
    publicExponent = new Uint8Array( js.Array(1,0,1)),
    hash = HashAlgorithm.`SHA-256`)

  val aSignAlgorithm:KeyAlgorithmIdentifier  = l(
    "name" ->"ECDSA",
    "namedCurve" -> "P-384"
  ).asInstanceOf[KeyAlgorithmIdentifier]

  val aAlgorithm:KeyAlgorithmIdentifier = js.Dynamic.
    literal("name" -> "RSA-OAEP").
    asInstanceOf[KeyAlgorithmIdentifier]



  val usageDecrypt=js.Array(KeyUsage.decrypt)
  val usageEncrypt=js.Array(KeyUsage.encrypt)
  val usageSign=js.Array(KeyUsage.sign)
  val usageVerify=js.Array(KeyUsage.verify)



  def generateASymetricEncryptionKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = generateKeysFor( js.Array(KeyUsage.encrypt, KeyUsage.decrypt),aHashAlgorithm)


  def generateSignKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = generateKeysFor(js.Array(KeyUsage.sign, KeyUsage.verify), aSignAlgorithm)




  private def generateKeysFor(aUsage:js.Array[KeyUsage],alg:KeyAlgorithmIdentifier)
                     (implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = crypto.subtle.generateKey(
    algorithm = alg,
    extractable = true,
    keyUsages = aUsage).
    toFuture.map(_.asInstanceOf[CryptoKeyPair])


  def importKeyPair(
                     keyPairNative: KeypairNative,
                     privateUsage:js.Array[KeyUsage],
                     publicUsage:js.Array[KeyUsage],
                     algo:KeyAlgorithmIdentifier
                   )(
                     implicit executionContext: ExecutionContext)=keyPairNative.
    `private`.map(aJSPrivateKey=>
    importKey(
      aJSPrivateKey, privateUsage,algo)).
    getOrElse(Future{None}).
    flatMap( (privateKeyOpt:Option[CryptoKey])=>keyPairNative.
      `public`.map(
      aJSPublicKey=>
      importKey(aJSPublicKey, publicUsage,algo).
        map(publicKeyOpt=>
        toKeyPair(privateKeyOpt,publicKeyOpt))).
      getOrElse(Future{toKeyPair(privateKeyOpt,None)}))






  def toKeyPair(privateKey: Option[CryptoKey], publicKey: Option[CryptoKey]) = js.Dictionary(
    "publicKey" -> publicKey.getOrElse(null),
    "privateKey" -> privateKey.getOrElse(null)
  ).asInstanceOf[CryptoKeyPair]



  def importKey(jsonWebKey: JsonWebKey,usages: js.Array[KeyUsage],algo:KeyAlgorithmIdentifier)(
    implicit executionContext: ExecutionContext) = crypto.subtle.importKey(
    KeyFormat.jwk,
      jsonWebKey,
      algo,
    extractable = true,
      usages).toFuture.
      map(k=>Some(k.asInstanceOf[CryptoKey]))



  def asymEncryptString(key:CryptoKey, s:String)
                       (implicit executionContext: ExecutionContext) =
    crypto.subtle.encrypt(aAlgorithm,
      key,
      new TextEncoder().encode(s)).
      toFuture.
      map(_.asInstanceOf[ArrayBuffer])

  def asymDecryptBuffer(key:CryptoKey, b:Buffer)
                     (implicit executionContext: ExecutionContext) =
    crypto.subtle.decrypt(aAlgorithm,
      key,
      b.asInstanceOf[org.scalajs.dom.crypto.BufferSource]).
      //Buffer.from(f)).
      toFuture.
      map(_.asInstanceOf[ArrayBuffer])

  def asymDecryptArrayBuffer(key:CryptoKey, b:ArrayBuffer)
                     (implicit executionContext: ExecutionContext) =
    crypto.subtle.decrypt(aAlgorithm,
      key,
      b).
      toFuture.
      map(_.asInstanceOf[ArrayBuffer])







  private def sign(keys:CryptoKeyPair,data:ArrayBuffer)
          (implicit executionContext: ExecutionContext)= crypto.subtle.sign(
    l(
      "name" -> "ECDSA",
      "hash" -> l("name" -> "SHA-384"),
    ).asInstanceOf[KeyAlgorithmIdentifier],
    keys.privateKey,
    data
  ).toFuture.map(_.asInstanceOf[ArrayBuffer])

  def signFile(keys:CryptoKeyPair,f:File)
              (implicit executionContext: ExecutionContext) =
    new FileReader().
    futureReadArrayBuffer(f).
      flatMap(b=>signArrayBuffer(keys, b))


   def signArrayBuffer(keys: CryptoKeyPair, b: ArrayBuffer)
                              (implicit executionContext: ExecutionContext)
  = {
    sign(keys, b).
      flatMap(signedB => signArrayBufferAddSigner(keys, b, signedB))
  }

  private def signArrayBufferAddSigner
  (keys: CryptoKeyPair, b: ArrayBuffer, signedB: ArrayBuffer)
  (implicit executionContext: ExecutionContext)
  = {
    exportCryptoKey(
      keys.publicKey).
      map(jk => Buffer.
        from(stringify(jk)).
        buffer.
        asInstanceOf[ArrayBuffer]).
      map(r => (b, signedB, r))
  }

  def verify(key:CryptoKey, signature:ArrayBuffer, data:ArrayBuffer)
            (implicit executionContext: ExecutionContext)= crypto.subtle.verify(
    l(
      "name" -> "ECDSA",
      "hash" -> l("name" -> "SHA-384"),
    ).asInstanceOf[KeyAlgorithmIdentifier],
    key,
    signature,
    data
  ).toFuture.map(_.asInstanceOf[Boolean])

}
