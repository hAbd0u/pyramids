package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.pcrypto.{AsymetricCrypto, SymetricCrypto, WalletNative}
import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.{ExecutionContext, Future}





trait KeyImport extends  SymetricCrypto with AsymetricCrypto {

  val pyramidConfig: PyramidConfig

  def importSymKey(walletNative:WalletNative)
                (implicit ctx:ExecutionContext)= walletNative.
    sym.
    map(webKey => importSymetricKey(webKey).
      map(k=>Some(k))).getOrElse(Future{None}).
    map(ko=> new Pyramid(pyramidConfig.copy(symKeyOpt = ko)))


  def importAsymKey(walletNative: WalletNative)(implicit  executionContext: ExecutionContext) = walletNative.
    asym.
    map(kp=>
      importKeyPair(
        keyPairNative = kp,
        privateUsage = usageDecrypt,
        publicUsage = usageEncrypt,aHashAlgorithm).
        map(e=>Some(e))).
    getOrElse(Future{None}).
    map(e=>new Pyramid(pyramidConfig.copy(asymKeyOpt = e)))


  def importTemporal(walletNative: WalletNative)(implicit  executionContext: ExecutionContext) =  walletNative.
    temporal.map(t=>Future{new Pyramid(pyramidConfig.withTemporalCredentials(t))}).getOrElse(Future{new Pyramid(pyramidConfig)})

  def importStellar(walletNative: WalletNative)(implicit  executionContext: ExecutionContext) = walletNative
    .wallets.
    flatMap(
      _.stellar.map(s=>Future{
        pyramidConfig.
          withStellarInternHash(s).
          pyramid()})).
    getOrElse(Future{pyramidConfig.pyramid()})

  /*
  class KeyImport: function importAsymKey, do the same for the signature keys: "importSignKey"
  It is the same as for the asymetric key. Try!
  * */
  def importSignKey(walletNative: WalletNative)(implicit  executionContext: ExecutionContext) = walletNative.sign.
    map(kp=>
      importKeyPair(
        keyPairNative = kp,
        privateUsage = usageSign,
        publicUsage = usageVerify,
        aSignAlgorithm
      ).
        map(e=>Some(e))).
    getOrElse(Future{None}).
    map(e=>new Pyramid(pyramidConfig.copy(signKeyOpt = e)))

  def importAllKeys(walletNative: WalletNative)(implicit  executionContext: ExecutionContext)=
    importSymKey(walletNative).
      flatMap(_.importAsymKey(walletNative)).
      flatMap(_.importSignKey(walletNative)).
      flatMap(_.importTemporal(walletNative)).
      flatMap(_.importStellar(walletNative))

}
