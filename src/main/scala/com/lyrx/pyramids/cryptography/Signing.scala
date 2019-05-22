package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{DistributedData, Pyramid, PyramidConfig}
import org.scalajs.dom.crypto.CryptoKeyPair

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait Signing extends  AsymetricCrypto  {

    val pyramidConfig: PyramidConfig



    def signArrayBuffer(data:ArrayBuffer)
            (implicit executionContext: ExecutionContext) = pyramidConfig.
      signKeyOpt.map(key=>sign(key,data).map(Some(_))).
      getOrElse(Future{None})

}
