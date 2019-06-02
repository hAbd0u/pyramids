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


  /*

    ._1:  unencrypted
    ._2: encrypted
    ._3: random data
    ._4 signature

   */
  def encryptAndSignFile(f:File) (implicit ctx:ExecutionContext) = {
     pyramidConfig.
      symKeyOpt.
      map(k=>symEncryptFile(k,f).flatMap(
        t=> pyramidConfig.signKeyOpt.
            map(signKeys=> sign(signKeys,t.encrypted.get).
              map(signature=>ZippableEncrypt(t.unencrypted,
                t.encrypted,
                t.random,
                Some(signature),
                t.metaData,
                t.metaRandom,
                t.signer
              ))
            ).getOrElse(Future{
          ZippableEncrypt(t.unencrypted,
          t.encrypted,
          t.random,
          None,
            t.metaData,
            t.metaRandom,
            t.signer)})
      )).getOrElse(
       pyramidConfig.signKeyOpt.map(signKeys=>
         signFile(signKeys,f).map(signatureTupel =>ZippableEncrypt(
           Some(signatureTupel._1),
           None,
           None,
           Some(signatureTupel._2),
           None,
           None,
           None))
       ).
         getOrElse(Future{ZippableEncrypt(None,None,None,None,None,None,None)})   )
  }

  def zipEncrypt(f:File) (implicit ctx:ExecutionContext) =
    encryptAndSignFile(f).map(_.zipped())


}
