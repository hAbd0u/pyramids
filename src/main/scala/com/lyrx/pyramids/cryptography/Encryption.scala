package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.File
import org.scalajs.dom.crypto.crypto

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}

trait Encryption extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def symEncrypt()(implicit ctx:ExecutionContext) = {
    val iv = crypto.getRandomValues(new Uint8Array(12))

    pyramidConfig.distributedData.unencryptedOpt.
      flatMap( (arrayBuffer:ArrayBuffer) => pyramidConfig.
        symKeyOpt.map(symKey=>
          crypto.
            subtle.
            encrypt(algorithmIdentifier(iv),
              symKey,
              arrayBuffer
            ).
          toFuture.
          map(_.asInstanceOf[ArrayBuffer]).
          map(b=>new Pyramid(pyramidConfig.copy(
            distributedData = pyramidConfig.
            distributedData.
            copy(bufferOpt = Some(b),
              ivOpt = Some(iv.buffer)))))
        )
      )//.getOrElse(Future.failed(new Throwable("No data found")))






  }

}
