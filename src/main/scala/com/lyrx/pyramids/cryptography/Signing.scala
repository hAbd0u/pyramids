package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait Signing extends  AsymetricCrypto  {

    val pyramidConfig: PyramidConfig

    def asymSign()(implicit ctx:ExecutionContext) = pyramidConfig.
      signKeyOpt.flatMap(signKeyPair=> pyramidConfig.
      distributedData.
    unencryptedOpt.
    map(buffer=>sign(signKeyPair,buffer)
      map((signBuffer:ArrayBuffer)=>
      new Pyramid(pyramidConfig.
        withSignature(signBuffer).
      msg("Oh pharao, we have signed your data!"))))
    ).getOrElse(Future{new Pyramid(
      pyramidConfig.
        msg("Oh Pharao, we have no signature key pair!"))})

}
