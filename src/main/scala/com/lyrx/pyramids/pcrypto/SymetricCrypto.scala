package com.lyrx.pyramids.pcrypto

import com.lyrx.pyramids.{DistributedData, DistributedDir, EitherData}
import org.scalajs.dom.crypto.{AlgorithmIdentifier, CryptoKey, JsonWebKey, KeyAlgorithmIdentifier, KeyFormat, KeyUsage, crypto}
import org.scalajs.dom.raw.{File, FileReader}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}
import js.Dynamic.{literal => l}
import PCryptoImplicits._



trait SymetricCrypto extends Crypto {
  val ALGORITHM = "AES-GCM"


  def algorithmIdentifier(iv:ArrayBufferView):AlgorithmIdentifier={
    l( "name" -> ALGORITHM,
      "iv" -> iv ).asInstanceOf[AlgorithmIdentifier]
  }
  val keyAlgorithmIdentifier:KeyAlgorithmIdentifier= l(
    "name" -> ALGORITHM,
    "length" -> 256).asInstanceOf[KeyAlgorithmIdentifier]



  def encryptDir(key:CryptoKey,distributedDir:DistributedDir) (implicit executionContext: ExecutionContext) = Future.
    sequence(
      distributedDir.
        data.
        map( encryptEither(key,_))).
    map(DistributedDir(_,distributedDir.name))


  def encrypt(symKey:CryptoKey,distributedData: DistributedData)
             (implicit ctx:ExecutionContext)=
    distributedData.unencryptedOpt.
      map(arrayBuffer => {
        val iv = crypto.getRandomValues(new Uint8Array(12))
        crypto.
          subtle.
          encrypt(algorithmIdentifier(iv),
            symKey,
            arrayBuffer
          ).
          toFuture.
          map(_.asInstanceOf[ArrayBuffer]).map(b=>distributedData.copy(
          bufferOpt = Some(b),
          ivOpt = Some(iv.buffer)))
      }).getOrElse(Future{distributedData})

  def encryptEither(key:CryptoKey,
                    eitherData: EitherData)(implicit executionContext: ExecutionContext):Future[EitherData] = eitherData match {
    case Left(data:DistributedData) => encrypt(key,data).map(f=>Left(f))
    case Right(dir:DistributedDir) => encryptEither(key,eitherData)
  }

  def metaDataFrom(f: File): Option[ArrayBuffer] = None

  def symEncryptFile(key:CryptoKey, f:File)
                    (implicit executionContext: ExecutionContext) =
    new FileReader().
    futureReadArrayBuffer(f).
    flatMap(unencryptedData=>{
      val iv = crypto.getRandomValues(new Uint8Array(12))
      crypto.
        subtle.
        encrypt(algorithmIdentifier(iv),
          key,
          unencryptedData
        ).
        toFuture.map(r=>EncryptedData(
        Some(unencryptedData),
        Some(r.asInstanceOf[ArrayBuffer]),
        Some(iv.buffer),
        None,
        metaDataFrom(f)
      ))
    })




  def decryptDir(key:CryptoKey,distributedDir:DistributedDir) (implicit executionContext: ExecutionContext) = Future.
    sequence(
    distributedDir.
    data.
    map( decryptEither(key,_))).
    map(DistributedDir(_,distributedDir.name))



  def decryptEither(key:CryptoKey,
                    eitherData: EitherData)
                   (implicit executionContext: ExecutionContext):Future[EitherData] =
    eitherData match {
    case Left(data:DistributedData) => decrypt(key,data).map(f=>Left(f))
    case Right(dir:DistributedDir) => decryptEither(key,eitherData)
  }

  def decrypt(key:CryptoKey,distributedData: DistributedData)
             (implicit executionContext: ExecutionContext)= distributedData.
    bufferOpt.flatMap(data => distributedData.ivOpt.
  map(iv =>  crypto.
    subtle.
    decrypt(algorithmIdentifier(new Uint8Array(iv,0,12)),
      key,
      data
    ).toFuture.
    map(aAny=>aAny.asInstanceOf[ArrayBuffer]).
  map(b=>distributedData.copy(unencryptedOpt = Some(b)))
  )).getOrElse(Future{distributedData})





  def generateSymmetricKey()(implicit ctx:ExecutionContext)= crypto.
    subtle.
    generateKey(
      keyAlgorithmIdentifier,
      true,
      js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)).
    toFuture.
    map(_.asInstanceOf[CryptoKey])

  def importSymetricKey(jsonWebKey: JsonWebKey)(
    implicit executionContext: ExecutionContext)=crypto.subtle.importKey(
    KeyFormat.jwk,
    jsonWebKey,
    keyAlgorithmIdentifier,
    true,
    js.Array(
      KeyUsage.encrypt,
      KeyUsage.decrypt)).
    toFuture.
    map(_.asInstanceOf[CryptoKey])



}
