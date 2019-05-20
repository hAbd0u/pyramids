package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.ExecutionContext

trait KeyCreation[T <: KeyCreation[T]] extends  SymetricCrypto with AsymetricCrypto with CanInstance[T] {

  val pyramidConfig: PyramidConfig

  def createSymKey()(implicit ctx:ExecutionContext) = generateSymmetricKey().
    map(key=> createInstance(
      pyramidConfig.
        copy(symKeyOpt = Some(key))))

  def createASymKeys()(implicit ctx:ExecutionContext) = generateASymetricEncryptionKeys().
    map(keyPair=> createInstance(
      pyramidConfig.
        copy(asymKeyOpt = Some(keyPair))))

  def createSignKeys()(implicit ctx:ExecutionContext) = generateSignKeys().
    map(keyPair=>createInstance(
      pyramidConfig.
        copy(signKeyOpt = Some(keyPair))))

  def generateKeys()(implicit ctx:ExecutionContext) = createSymKey().
    flatMap(_.createASymKeys()).
    flatMap(_.createSignKeys().map(_.msg("Oh Pharao, you have new keys!")))


}
