package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.pcrypto.CryptoTypes.EncryptionResult
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
  def encryptAndSignFile(f:File) (implicit ctx:ExecutionContext): Future[ZippableEncrypt] = {
     pyramidConfig.
      symKeyOpt.
      map(k=>symEncryptFile(k,f).flatMap(
        t=> pyramidConfig.signKeyOpt.
            map(signKeys=> sign(signKeys,t._1).
              map(signature=>ZippableEncrypt(Some(t._1),
                Some(t._2),
                Some(t._3),
                Some(signature)))
            ).getOrElse(Future{
          ZippableEncrypt(Some(t._1),
          Some(t._2),
          Some(t._3),
          None)})
      )).getOrElse(
       pyramidConfig.signKeyOpt.map(signKeys=>
         signFile(signKeys,f).map(signatureTupel =>ZippableEncrypt(
           Some(signatureTupel._1),
           None,
           None,
           Some(signatureTupel._2)))
       ).
         getOrElse(Future{ZippableEncrypt(None,None,None,None)})   )
  }

  def zipEncrypt(f:File) (implicit ctx:ExecutionContext) =
    encryptAndSignFile(f).map(_.zipped())


}
