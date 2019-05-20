package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.File
import org.scalajs.dom.crypto.crypto

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}

trait Encryption extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def symEncrypt()(implicit ctx:ExecutionContext) =  pyramidConfig.
        symKeyOpt.map(symKey=> encrypt(symKey,pyramidConfig.distributedData)).
    map(_.map(d=>new Pyramid(
      pyramidConfig.copy(distributedData = d).
        msg("Your data has been encryted, oh Pharao!")
    ))).
    getOrElse(Future{new Pyramid(pyramidConfig.msg("Oh pharao, we have not found your encryption key!"))})


}
