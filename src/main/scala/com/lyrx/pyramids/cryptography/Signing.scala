package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{DistributedData, Pyramid, PyramidConfig}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait Signing extends  AsymetricCrypto  {

    val pyramidConfig: PyramidConfig



}
