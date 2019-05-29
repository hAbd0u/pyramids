package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, SymetricCrypto}
import org.scalajs.dom.raw.File
import typings.jszipLib.jszipMod.JSZip

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobal, JSImport}



trait Encryption extends  SymetricCrypto with AsymetricCrypto {

  val pyramidConfig: PyramidConfig

  /*
  def symEncrypt()(implicit ctx:ExecutionContext) =  pyramidConfig.
        symKeyOpt.map(symKey=> encryptDir(symKey,pyramidConfig.distributedDir)).
    map(_.map(d=>new Pyramid(
      pyramidConfig.copy(distributedDir = d).
        msg("Your data has been encryted, oh Pharao!")
    ))).
    getOrElse(Future{new Pyramid(pyramidConfig.msg("Oh pharao, we have not found your encryption key!"))})


   */

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
  def encryptAndSignFile(f:File) (implicit ctx:ExecutionContext): Future[EncryptionResult] = {
     pyramidConfig.
      symKeyOpt.
      map(k=>symEncryptFile(k,f).flatMap(
        t=> pyramidConfig.signKeyOpt.
            map(signKeys=> sign(signKeys,t._1).
              map(signature=>Encrypted(Some(t._1),
                Some(t._2),
                Some(t._3),
                Some(signature)))
            ).getOrElse(Future{
          Encrypted(Some(t._1),
          Some(t._2),
          Some(t._3),
          None)})
      )).getOrElse(
       pyramidConfig.signKeyOpt.map(signKeys=>
         signFile(signKeys,f).map(signatureTupel =>Encrypted(
           Some(signatureTupel._1),
           None,
           None,
           Some(signatureTupel._2)))
       ).
         getOrElse(Future{Encrypted(None,None,None,None)})   )
  }

  def zipEncrypt(f:File) (implicit ctx:ExecutionContext) =
    encryptAndSignFile(f).map(_.zipped())


}
