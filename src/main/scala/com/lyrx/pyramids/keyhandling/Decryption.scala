package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.pcrypto.SymetricCrypto
import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.File

import scala.concurrent.{ExecutionContext, Future}

trait Decryption  extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  /*
  def symDecrypt()(implicit ctx:ExecutionContext):Future[Pyramid] =  pyramidConfig.
    symKeyOpt.map(symKey=> decryptDir(symKey,pyramidConfig.distributedDir)).
    map(_.map(d=>new Pyramid(
      pyramidConfig.copy(distributedDir = d).
        msg("Your data has been decryted, oh Pharao!")
    ))).
    getOrElse(Future{new Pyramid(pyramidConfig.msg("Oh pharao, we have not found your decryption key!"))})
*/



}
