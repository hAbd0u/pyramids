package com.lyrx.pyramids

import java.security.KeyPair

import com.lyrx.pyramids.PyramidCrypt.{AsymetricCrypto, SymetricCrypto, WalletNative}
import org.scalajs.dom.crypto.{CryptoKeyPair, JsonWebKey}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal

object  Pyramid{
  def apply()=new Pyramid(PyramidConfig(None,None,None))

}
class Pyramid(override val pyramidConfig: PyramidConfig)
  extends KeyCreation
    with KeyExport
    with KeyImport {




}
