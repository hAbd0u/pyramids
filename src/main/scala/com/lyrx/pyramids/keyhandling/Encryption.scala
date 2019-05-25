package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.pcrypto.SymetricCrypto
import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.{ExecutionContext, Future}

trait Encryption extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def symEncrypt()(implicit ctx:ExecutionContext) =  pyramidConfig.
        symKeyOpt.map(symKey=> encryptDir(symKey,pyramidConfig.distributedDir)).
    map(_.map(d=>new Pyramid(
      pyramidConfig.copy(distributedDir = d).
        msg("Your data has been encryted, oh Pharao!")
    ))).
    getOrElse(Future{new Pyramid(pyramidConfig.msg("Oh pharao, we have not found your encryption key!"))})


}
