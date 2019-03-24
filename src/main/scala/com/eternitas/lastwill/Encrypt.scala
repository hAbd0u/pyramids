package com.eternitas.lastwill



import org.scalajs.dom
import dom.crypto.{CryptoKey, CryptoKeyPair, HashAlgorithm, JsonWebKey, KeyFormat, KeyUsage, RsaHashedKeyAlgorithm, crypto}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.JSON
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
          map(privateJw=>js.Dynamic.literal(
            "private" ->privateJw,
            "public" -> publicJw)
          )).flatten
  ).
    getOrElse(Future{js.Dynamic.literal("empty" -> true)}).
    map(ee=>js.JSON.stringify(ee:js.Any,null:js.Array[js.Any],1:js.Any))

}


object Encrypt {

  def importJSON(json:Dynamic)={

    val pKey:JsonWebKey = json("public")
    println("pKey: " + JSON.stringify(pKey))

    //val p = js.JSON.parse(json)

  }

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
