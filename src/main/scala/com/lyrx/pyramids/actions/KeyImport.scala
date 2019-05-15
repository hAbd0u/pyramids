package com.lyrx.pyramids.actions

import com.lyrx.pyramids.PyramidCrypt.{AsymetricCrypto, SymetricCrypto, WalletNative}
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
        publicUsage = usageEncrypt).
        map(e=>Some(e))).
    getOrElse(Future{None}).
    map(e=>new Pyramid(pyramidConfig.copy(asymKeyOpt = e)))



}
