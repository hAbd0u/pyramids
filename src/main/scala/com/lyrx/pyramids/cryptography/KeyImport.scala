package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.{ExecutionContext, Future}

trait KeyImport[T<: KeyImport[T]] extends  SymetricCrypto with AsymetricCrypto with CanInstance[T] {

  val pyramidConfig: PyramidConfig




  def importSymKey(walletNative:WalletNative)
                (implicit ctx:ExecutionContext)= walletNative.
    sym.
    map(webKey => importSymetricKey(webKey).
      map(k=>Some(k))).getOrElse(Future{None}).
    map(ko=> createInstance(pyramidConfig.copy(symKeyOpt = ko)))


  def importAsymKey(walletNative: WalletNative)(implicit  executionContext: ExecutionContext) = walletNative.
    asym.
    map(kp=>
      importKeyPair(
        keyPairNative = kp,
        privateUsage = usageDecrypt,
        publicUsage = usageEncrypt).
        map(e=>Some(e))).
    getOrElse(Future{None}).
    map(e=>createInstance(pyramidConfig.copy(asymKeyOpt = e)))



  /*
  class KeyImport: function importAsymKey, do the same for the signature keys: "importSignKey"
  It is the same as for the asymetric key. Try!
  * */
  def importSignKey(walletNative: WalletNative)(implicit  executionContext: ExecutionContext) = walletNative.sign.
    map(kp=>
      importKeyPair(
        keyPairNative = kp,
        privateUsage = usageSign,
        publicUsage = usageVerify).
        map(e=>Some(e))).
    getOrElse(Future{None}).
    map(e=>createInstance(pyramidConfig.copy(signKeyOpt = e)))

  def importAllKeys(walletNative: WalletNative)(implicit  executionContext: ExecutionContext)=
    importSymKey(walletNative).
      flatMap(_.importAsymKey(walletNative)).
      flatMap(_.importSignKey(walletNative))

}
