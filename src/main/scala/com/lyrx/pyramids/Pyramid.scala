package com.lyrx.pyramids

import com.lyrx.pyramids.PyramidCrypt.{AsymetricCrypto, SymetricCrypto}

import scala.concurrent.ExecutionContext


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(None,None,None))

}
class Pyramid(val pyramidConfig: PyramidConfig) extends SymetricCrypto with AsymetricCrypto{

  def createSymKey()(implicit ctx:ExecutionContext) = generateSymmetricKey().
      map(key=> new Pyramid(
        pyramidConfig.
          copy(symKeyOpt = Some(key))))

  def createASymKeys()(implicit ctx:ExecutionContext) = generateASymetricEncryptionKeys().
    map(keyPair=> new Pyramid(
      pyramidConfig.
        copy(asymKeyOpt = Some(keyPair))))

  def createSignKeys()(implicit ctx:ExecutionContext) = generateSignKeys().
    map(keyPair=> new Pyramid(
      pyramidConfig.
        copy(signKeyOpt = Some(keyPair))))

  def generateKeys()(implicit ctx:ExecutionContext) = createSymKey().
    flatMap(_.createASymKeys()).
    flatMap(_.createSignKeys())




}
