package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.pcrypto.AsymetricCrypto

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait Signing extends  AsymetricCrypto  {

    val pyramidConfig: PyramidConfig



    def signArrayBuffer(data:ArrayBuffer)
            (implicit executionContext: ExecutionContext) = pyramidConfig.
      signKeyOpt.map(key=>sign(key,data).map(Some(_))).
      getOrElse(Future{None})

}
