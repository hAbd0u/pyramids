package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.jszip.ZippableEncrypt
import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, CryptoTypes, SymetricCrypto}
import org.scalajs.dom.raw.File
import typings.stdLib.CryptoKey

import scala.concurrent.{ExecutionContext, Future}



trait Encryption extends  SymetricCrypto with AsymetricCrypto  {

  val pyramidConfig: PyramidConfig


  def encryptFile(f:File) (implicit ctx:ExecutionContext)= pyramidConfig.
    symKeyOpt.
    map(k=>symEncryptFile(k,f).map(Some(_))).
    getOrElse(Future{None})



  def encryptAndSignFileDefault(f:File) (implicit ctx:ExecutionContext) =
    encryptAndSignFile(f,pyramidConfig.symKeyOpt)

  def encryptAndSignFile(f:File,symKeyOpt:Option[CryptoTypes.PyramidCryptoKey])
                               (implicit ctx:ExecutionContext) = {
    symKeyOpt.
      map(k=>symEncryptFile(k,f).flatMap( //encryption keys present:
        t=> pyramidConfig.signKeyOpt. // sign keys present:
          map(signKeys=> signArrayBuffer(signKeys,t.encrypted.get).
          map(t2=>ZippableEncrypt(
            unencrypted = t.unencrypted,
            encrypted = t.encrypted,
            random = t.random,
            signature = Some(t2._2),
            metaData = t.metaData,
            metaRandom = t.metaRandom,
            signer = Some(t2._3)
          ))
        ).getOrElse(Future{ // no signature keys:
          ZippableEncrypt(
            unencrypted = t.unencrypted,
            encrypted = t.encrypted,
            random = t.random,
            signature = None,
            metaData = t.metaData,
            metaRandom = t.metaRandom,
            signer = t.signer)})
      )).getOrElse( // no encryption keys:
      pyramidConfig.signKeyOpt.map(signKeys=>  // signature keys present:
        signFile(signKeys,f).map(signatureTupel =>ZippableEncrypt(
          unencrypted = Some(signatureTupel._1),
          encrypted = None,
          random =  None,
          signature = Some(signatureTupel._2),
          metaData = None,
          metaRandom = None,
          signer = Some(signatureTupel._3)))
      ).
        getOrElse( // No signature keys;
          Future{
            ZippableEncrypt(None,None,None,None,None,None,None)})
    )
  }





}
