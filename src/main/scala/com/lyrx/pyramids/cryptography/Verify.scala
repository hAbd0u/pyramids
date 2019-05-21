package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.PyramidConfig

import scala.concurrent.ExecutionContext

trait Verify extends  AsymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def verify()(implicit ctx:ExecutionContext)

}
