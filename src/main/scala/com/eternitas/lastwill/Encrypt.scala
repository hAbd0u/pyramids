package com.eternitas.lastwill



import org.scalajs.dom
import dom.crypto.{CryptoKey, CryptoKeyPair, HashAlgorithm, JsonWebKey, KeyFormat, KeyUsage, RsaHashedKeyAlgorithm, crypto}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array



object Encrypt {
  val aKeyFormat =  KeyFormat.jwk
  val aAlgorithm =  RsaHashedKeyAlgorithm.`RSA-OAEP`(modulusLength = 4096,
    publicExponent = new Uint8Array( js.Array(1,0,1)),
    hash = HashAlgorithm.`SHA-256`)
    val usages = js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)
  val usageDecrypt = js.Array(
    KeyUsage.decrypt)
  val usageEncrypt = js.Array(
    KeyUsage.encrypt)



  def importJSON(eternitas: Eternitas,
                 json:js.Dynamic,
                 cb:(Eternitas)=>Unit)(
    implicit executionContext: ExecutionContext)=crypto.subtle.importKey(
       aKeyFormat,
       json.`public`.asInstanceOf[JsonWebKey],
       aAlgorithm,
       true,
       usageEncrypt
     ).toFuture.onComplete(_.map(aAny=>{
         val publicKey = aAny.asInstanceOf[CryptoKey]
         crypto.subtle.importKey(
           aKeyFormat,
           json.`private`.asInstanceOf[JsonWebKey],
           aAlgorithm,
           true,
           usageDecrypt
         ).toFuture.onComplete(_.map(aAny2=>{
           val privateKey = aAny2.asInstanceOf[CryptoKey]
           cb(new Eternitas(
           Some(js.Dictionary(
             "publicKey"->publicKey,
             "privateKey" -> privateKey
           ).asInstanceOf[CryptoKeyPair]),
             pinnataOpt = eternitas.pinnataOpt))
         }))})

     )



  def eexportKey(key: CryptoKey)
                (implicit ctx:ExecutionContext) = crypto.subtle.
        exportKey(aKeyFormat,key).
        toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])




  def generateKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = crypto.subtle.generateKey(
    algorithm = aAlgorithm,
    extractable = true,
    keyUsages = usages).
    toFuture.map(_.asInstanceOf[CryptoKeyPair])




}
