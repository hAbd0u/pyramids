package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.jszip.ZippableEncrypt
import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, SymetricCrypto}
import org.scalajs.dom.raw.File

import scala.concurrent.{ExecutionContext, Future}



trait Encryption extends  SymetricCrypto with AsymetricCrypto {

  val pyramidConfig: PyramidConfig


  def encryptFile(f:File) (implicit ctx:ExecutionContext)= pyramidConfig.
    symKeyOpt.
    map(k=>symEncryptFile(k,f).map(Some(_))).
    getOrElse(Future{None})



  def encryptAndSignFile(f:File) (implicit ctx:ExecutionContext) = {
     pyramidConfig.
      symKeyOpt.
      map(k=>symEncryptFile(k,f).flatMap( //encryption keys present:
        t=> pyramidConfig.signKeyOpt. // sign keys present:
            map(signKeys=> sign(signKeys,t.encrypted.get).
              map(signature=>ZippableEncrypt(t.unencrypted,
                t.encrypted,
                t.random,
                Some(signature),
                t.metaData,
                t.metaRandom,
                t.signer
              ))
            ).getOrElse(Future{ // no signature keys:
          ZippableEncrypt(t.unencrypted,
          t.encrypted,
          t.random,
          None,
            t.metaData,
            t.metaRandom,
            t.signer)})
      )).getOrElse( // no encryption keys:
       pyramidConfig.signKeyOpt.map(signKeys=>  // signature keys present:
         signFile(signKeys,f).map(signatureTupel =>ZippableEncrypt(
           Some(signatureTupel._1),
           None,
           None,
           Some(signatureTupel._2),
           None,
           None,
           None))
       ).
         getOrElse( // No signature keys;
           Future{
             ZippableEncrypt(None,None,None,None,None,None,None)})
     )
  }

  def zipEncrypt(f:File) (implicit ctx:ExecutionContext) =
    encryptAndSignFile(f).map(_.zipped())


}
