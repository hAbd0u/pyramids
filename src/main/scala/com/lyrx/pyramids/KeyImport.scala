package com.lyrx.pyramids

import com.lyrx.pyramids.PyramidCrypt.{AsymetricCrypto, SymetricCrypto, WalletNative}

import scala.concurrent.{ExecutionContext, Future}

trait KeyImport extends  SymetricCrypto with AsymetricCrypto {

  val pyramidConfig: PyramidConfig

  def importSymKey(walletNative:WalletNative)
                (implicit ctx:ExecutionContext)= walletNative.
    sym.
    map(webKey => importSymetricKey(webKey).
      map(k=>Some(k))).getOrElse(Future{None}).
    map(ko=> new Pyramid(pyramidConfig.copy(symKeyOpt = ko)))




}
