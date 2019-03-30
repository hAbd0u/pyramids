package com.eternitas.lastwill.cryptoo


import com.eternitas.lastwill.{Eternitas, UserFeedback}
import org.scalajs.dom.crypto.{AesGcmParams, Algorithm, AlgorithmIdentifier, CryptoKey, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, crypto}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}

object SymCrypto extends SymCryptoTrait {

  override val aKeyFormat =  KeyFormat.jwk
  override val keyAlgorithmIdentifier:KeyAlgorithmIdentifier =l(

    "name" -> "AES-GCM",
    "length" -> 256
  ).asInstanceOf[KeyAlgorithmIdentifier]

}


case class SymEncryptionResult(result:ArrayBuffer,iv: ArrayBuffer)




trait SymCryptoTrait {

  val ALG = "AES-GCM"

  val aKeyFormat:KeyFormat
  val encryptDecrypt= js.Array(
    KeyUsage.encrypt,
    KeyUsage.decrypt)

  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier
  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={

    l( "name" -> ALG,
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }
  def createKeyAlgorithmIdentifier():KeyAlgorithmIdentifier={

    l( "name" -> ALG).asInstanceOf[KeyAlgorithmIdentifier]
  }



  def generateKey()(implicit ctx:ExecutionContext)= crypto.
    subtle.
    generateKey(keyAlgorithmIdentifier,true, encryptDecrypt).
    toFuture.
    map(_.asInstanceOf[CryptoKey])


  def test(key:CryptoKey,testString:String)
          (implicit executionContext: ExecutionContext,userFeedback: UserFeedback)= {
    import com.eternitas.lastwill.Buffers._;

   val f1 = encrypt(key,testString.toArrayBuffer())
    f1.onComplete(t=>t.map(r=>{
      val f2 = decrypt(key,r.result,r.iv)
      f2.onComplete((t2=>t2.map(r2=>{
        assert(r2.toNormalString().equals(testString))
        userFeedback.logString(
          "Test symmetric encryption successfull!"
        )
      })))
      f2.failed.map(e=>userFeedback.error("Test decryption failed: "+e.getLocalizedMessage))
    }))
    f1.failed.map(e=>userFeedback.error("Test encryption failed: "+e.getLocalizedMessage))

  }


  def encrypt(key:CryptoKey,data:ArrayBuffer)
             (implicit executionContext: ExecutionContext):Future[SymEncryptionResult]= {
    val iv:ArrayBufferView = crypto.getRandomValues(new Uint8Array(12))
    crypto.
      subtle.
      encrypt(algorithmIdentifier(iv),
        key,
        data
      ).toFuture.
      map(aAny=>SymEncryptionResult(aAny.asInstanceOf[ArrayBuffer],iv.buffer))
  }

  def decrypt(key:CryptoKey,data:ArrayBuffer,iv:ArrayBuffer)
             (implicit executionContext: ExecutionContext):Future[ArrayBuffer]= {
    crypto.
      subtle.
      decrypt(algorithmIdentifier(new Uint8Array(iv,0,12)),
        key,
        data
      ).toFuture.
      map(aAny=>aAny.asInstanceOf[ArrayBuffer])
  }


  def importKey(eternitas: Eternitas,
                    json:js.Dynamic,
                    cb:(Eternitas)=>Unit)(
                     implicit executionContext: ExecutionContext,
                     userFeedback: UserFeedback)={

   val f = crypto.subtle.importKey(
     aKeyFormat,
     json.sym.asInstanceOf[JsonWebKey],
     createKeyAlgorithmIdentifier(),
     true,
     encryptDecrypt).
     toFuture.
     map(_.asInstanceOf[CryptoKey])

    f.onComplete(t=>t.map((cryptoKey:CryptoKey)=>{
      userFeedback.logString("Loaded sym key.")
      cb(eternitas.addKey(cryptoKey))

    }))
    f.failed.map(e=>userFeedback.error(s"Import of sym key failed: "+e.getLocalizedMessage))


     //onComplete(t=$)
  }




  def eexportKey(key: CryptoKey)
                (implicit ctx:ExecutionContext) = crypto.subtle.
    exportKey(aKeyFormat,key).
    toFuture.map((a:Any)=>a.asInstanceOf[JsonWebKey])



}
