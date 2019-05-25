package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.pcrypto.AsymetricCrypto

import scala.concurrent.ExecutionContext

trait Verify extends  AsymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def verify()(implicit ctx:ExecutionContext)

}
