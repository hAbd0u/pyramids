package com.eternitas.lastwill



import org.scalajs.dom
import dom.crypto.{CryptoKey, HashAlgorithm, KeyUsage, RsaHashedKeyAlgorithm, crypto}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array



case class Eternitas(keysOpt:Option[CryptoKey] =None) {



  def withKeys()(implicit ctx:ExecutionContext) = {
    if(keysOpt.isEmpty) Encrypt.generateKeys().map(key=>this.copy(keysOpt=Some(key)))
    else
      Future.successful(this.copy(keysOpt=Some(keysOpt.get)))

  }




}


object Encrypt {



  def generateKeys()(implicit ctx:ExecutionContext):Future[CryptoKey]
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
    toFuture.map(_.asInstanceOf[CryptoKey])




}
