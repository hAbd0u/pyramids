package com.eternitas.lastwill.cryptoo

import com.eternitas.lastwill.{Eternitas, AllCredentials, UserFeedback}
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, HashAlgorithm, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, RsaHashedKeyAlgorithm, crypto}
import org.scalajs.dom.raw.File

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import js.Dynamic.{literal => l}



object AsymCrypto {
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

  val usageDecrypt = js.Array(
    KeyUsage.decrypt)
  val usageEncrypt = js.Array(
    KeyUsage.encrypt)


  val usages = js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt
  )

  val signUsages = js.Array(
    KeyUsage.sign,
    KeyUsage.verify
  )



  def encrypt(keys:CryptoKeyPair,data:ArrayBuffer)
             (implicit executionContext: ExecutionContext)= {

    crypto.
      subtle.
      encrypt(aHashAlgorithm,
        keys.publicKey,
        data
        //new Uint8Array(js.Array(3,4,5,6))
      ).toFuture.
      map(aAny=>aAny.asInstanceOf[ArrayBuffer])
  }





  def importCredentials(eternitas: Eternitas,
                        walletNative: WalletNative): Eternitas ={

    walletNative.credentials.map(p=>new Eternitas(
        eternitas.keyPairOpt,
        Some(
          AllCredentials(
            pinataApi = p.pinataApi.getOrElse(""),
            pinataApiSecret = p.pinataApiSecret.getOrElse(""),
            stampdApi = p.stampdApi.getOrElse(""),
            stampdApiSecret = p.stampdApiSecret.getOrElse("")

          )
        ),
        eternitas.keyOpt,
        eternitas.pinDataOpt,
      eternitas.signKeyOpt,
      eternitas.titleOpt
    )).getOrElse(eternitas)
  }

  def importKeyPair(file:File,
                    eternitas: Eternitas,
                    walletNative:WalletNative,
                    cb:(Eternitas)=>Unit)(
    implicit executionContext: ExecutionContext,
    userFeedback: UserFeedback)=walletNative.
    asym.map(kp=> kp.`public`.map(apublicKey=>crypto.subtle.importKey(
       aKeyFormat,
      apublicKey,
       aHashAlgorithm,
       true,
       usageEncrypt
     ).toFuture.onComplete(t=>{
    t.failed.map(e=>println("Error importing public key: " + e.getMessage))
    t.map(aAny=>{
      val publicKey = aAny.asInstanceOf[CryptoKey]
      kp.`private`.map(aprivkey=>
      crypto.subtle.importKey(
        aKeyFormat,
        aprivkey,
        aHashAlgorithm,
        true,
        usageDecrypt
      ).toFuture.onComplete(t2=>{
        t2.failed.map(e=>println("Error importing private key: " + e.getMessage))
        t2.map(aAny2=>{
          val privateKey = aAny2.asInstanceOf[CryptoKey]
          cb(eternitas.addKeyPair(privateKey,publicKey))})}))
    })})))


  def importSignKeyPair(file:File,
                    eternitas: Eternitas,
                    walletNative:WalletNative,
                    cb:(Eternitas)=>Unit)(
                     implicit executionContext: ExecutionContext,
                     userFeedback: UserFeedback)=walletNative.
    asym.map(kp=> kp.`public`.map(apublicKey=>crypto.subtle.importKey(
    aKeyFormat,
    apublicKey,
    aHashAlgorithm,
    true,
    usageEncrypt
  ).toFuture.onComplete(t=>{
    t.failed.map(e=>println("Error importing public key: " + e.getMessage))
    t.map(aAny=>{
      val publicKey = aAny.asInstanceOf[CryptoKey]
      kp.`private`.map(aprivkey=>
        crypto.subtle.importKey(
          aKeyFormat,
          aprivkey,
          aHashAlgorithm,
          true,
          usageDecrypt
        ).toFuture.onComplete(t2=>{
          t2.failed.map(e=>println("Error importing private key: " + e.getMessage))
          t2.map(aAny2=>{
            val privateKey = aAny2.asInstanceOf[CryptoKey]
            cb(eternitas.addKeyPair(privateKey,publicKey))})}))
    })})))








  def eexportKey(key: CryptoKey)
                (implicit ctx:ExecutionContext) = crypto.subtle.
        exportKey(aKeyFormat,key).
        toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])




  def generateKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = generateKeysFor(usages,aHashAlgorithm)


  def generateSignKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = generateKeysFor(signUsages, aSignAlgorithm)

  def generateKeysFor(aUsage:js.Array[KeyUsage],alg:KeyAlgorithmIdentifier)(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = crypto.subtle.generateKey(
    algorithm = alg

      /*
      l(
      "name" ->"ECDSA",
      "namedCurve" -> "P-384"
    ).asInstanceOf[KeyAlgorithmIdentifier]
    */

    ,
    extractable = true,
    keyUsages = aUsage).
    toFuture.map(_.asInstanceOf[CryptoKeyPair])




}
