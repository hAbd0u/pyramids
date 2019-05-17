package com.lyrx.pyramids.actions

import com.lyrx.pyramids.PyramidCrypt.{AsymetricCrypto, SymetricCrypto}
import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.ExecutionContext

trait KeyCreation extends  SymetricCrypto with AsymetricCrypto {

  val pyramidConfig: PyramidConfig

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
    flatMap(_.createSignKeys()).
    map(_.msg("Oh Pharao, you have new keys!"))


}
