package com.eternitas.lastwill



import org.scalajs.dom
import dom.crypto.{CryptoKey, CryptoKeyPair, HashAlgorithm, JsonWebKey, KeyFormat, KeyUsage, RsaHashedKeyAlgorithm, crypto}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import scala.util.Try



 class Eternitas(val keysOpt:Option[CryptoKeyPair] =None) {



  def withKeys()(implicit ctx:ExecutionContext) = {
    if(keysOpt.isEmpty) Encrypt.generateKeys().map(key=>new Eternitas(keysOpt=Some(key)))
    else
      Future.successful(new Eternitas(keysOpt=Some(keysOpt.get)))

  }


  def exportKeyJWKPublic()(implicit ctx:ExecutionContext) = keysOpt.map(
    key=>Encrypt.exportKeyJWK(key.publicKey)
  )

  def export()(implicit ctx:ExecutionContext) = keysOpt.map(
    key=>Encrypt
      .exportKeyJWK(key.publicKey)
      .map(publicJw=>
        Encrypt.
          exportKeyJWK(key.privateKey).
          map(privateJw=>new EternitasExport(privateJw,publicJw))).flatten
  ).
    getOrElse(Future{new EternitasExport(null,null)}).
    map(ee=>js.JSON.stringify(ee))

}


object Encrypt {

  def exportKeyJWK(key: CryptoKey)
                  (implicit ctx:ExecutionContext) = crypto.subtle.
        exportKey(KeyFormat.jwk,key).
        toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])




  def generateKeys()(implicit ctx:ExecutionContext):Future[CryptoKeyPair]
  = crypto.subtle.generateKey(
    algorithm = RsaHashedKeyAlgorithm.
      `RSA-OAEP`(modulusLength = 4096,
        publicExponent = new Uint8Array( js.Array(1,0,1)),
        hash = HashAlgorithm.`SHA-256`
      ),
    extractable = true,
    keyUsages = js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)).
    toFuture.map(_.asInstanceOf[CryptoKeyPair])




}
