package com.eternitas.lastwill.cryptoo

import com.eternitas.lastwill.{Eternitas, PinataAuth, UserFeedback}
import org.scalajs.dom.crypto.{CryptoKey, CryptoKeyPair, HashAlgorithm, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, RsaHashedKeyAlgorithm, crypto}
import org.scalajs.dom.raw.File

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}

object AsymCrypto {
  val aKeyFormat =  KeyFormat.jwk
  val aHashAlgorithm =  RsaHashedKeyAlgorithm.`RSA-OAEP`(modulusLength = 4096,
    publicExponent = new Uint8Array( js.Array(1,0,1)),
    hash = HashAlgorithm.`SHA-256`)

  val aAlgorithm:KeyAlgorithmIdentifier = js.Dynamic.
    literal("name" -> "RSA-OAEP").
    asInstanceOf[KeyAlgorithmIdentifier]



  val usages = js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)
  val usageDecrypt = js.Array(
    KeyUsage.decrypt)
  val usageEncrypt = js.Array(
    KeyUsage.encrypt)



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





  def importPinata(eternitas: Eternitas,
                   walletNative: WalletNative): Eternitas ={

    walletNative.pinata.map(p=>new Eternitas(
        eternitas.keyPairOpt,
        Some(
          PinataAuth(p.api.getOrElse(""),
            p.apisecret.getOrElse(""))
        ),
        eternitas.keyOpt,
        eternitas.pinDataOpt)).getOrElse(eternitas)
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
          userFeedback.message(s"YOUR DATA FROM ${file.name.toUpperCase}")
          cb(eternitas.addKeyPair(privateKey,publicKey))})}))
    })})))



  def eexportKey(key: CryptoKey)
                (implicit ctx:ExecutionContext) = crypto.subtle.
        exportKey(aKeyFormat,key).
        toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])




  def generateKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = crypto.subtle.generateKey(
    algorithm = aHashAlgorithm,
    extractable = true,
    keyUsages = usages).
    toFuture.map(_.asInstanceOf[CryptoKeyPair])




}
