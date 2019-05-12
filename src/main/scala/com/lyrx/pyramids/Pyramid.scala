package com.lyrx.pyramids

import com.lyrx.pyramids.PyramidCrypt.SymmetricCrypto

import scala.concurrent.ExecutionContext


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(None))

}
class Pyramid(val pyramidConfig: PyramidConfig) extends SymmetricCrypto{

  def createSymKey()(implicit ctx:ExecutionContext) = generateSymmetricKey().
      map(key=> new Pyramid(
        pyramidConfig.
          copy(symKeyOpt = Some(key))))



}
