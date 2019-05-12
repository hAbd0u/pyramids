package com.lyrx.pyramids

import java.security.KeyPair

import com.lyrx.pyramids.PyramidCrypt.{AsymetricCrypto, SymetricCrypto}
import org.scalajs.dom.crypto.{CryptoKeyPair, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}


object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(None,None,None))

}
class Pyramid(val pyramidConfig: PyramidConfig) extends SymetricCrypto with AsymetricCrypto{


  type JSKeyPairOpt = Option[(JsonWebKey,JsonWebKey)]
  type JSKeyOpt = Option[JsonWebKey]
  type FutureAllJSKeysOpt = Future[(JSKeyOpt,JSKeyPairOpt,JSKeyPairOpt)]

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


  def exportSymKey()(implicit ctx:ExecutionContext)=pyramidConfig.
    symKeyOpt.
    map(k=>exportCryptoKey(k).
      map(jw=>Some(jw))).
    getOrElse(Future{None})


  def exportASymKeys()(implicit ctx:ExecutionContext)=exportKeys(pyramidConfig.
    asymKeyOpt)

  def exportSignKeys()(implicit ctx:ExecutionContext)=exportKeys(pyramidConfig.
    signKeyOpt)


  def exportAllKeys()(implicit ctx:ExecutionContext):FutureAllJSKeysOpt = exportSymKey().
    flatMap(symKeyOpt=>exportASymKeys().map(keysOpt=>(symKeyOpt,keysOpt))).
    flatMap(keysOpt=>exportSignKeys().map(keysOpt2=>(keysOpt._1,keysOpt._2,keysOpt2)))



  def exportKeys(keyPairOpt:Option[CryptoKeyPair])(implicit ctx:ExecutionContext)=keyPairOpt.
    map(keys=>exportCryptoKey(keys.publicKey).
      flatMap(pubKeyJsonWeb=>exportCryptoKey(
        keys.privateKey
      ).flatMap(privKeyJsonWeb=>Future{Some((privKeyJsonWeb,pubKeyJsonWeb))})
      )
    ).
    getOrElse(Future{None})




}
