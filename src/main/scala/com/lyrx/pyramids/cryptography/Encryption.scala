package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.File

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.ArrayBuffer

trait Encryption extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def symEncrypt()(implicit ctx:ExecutionContext):Future[Pyramid] = {

    pyramidConfig.distributedData.unencryptedOpt.
      map( (arrayBuffer:ArrayBuffer) => {
        pyramidConfig.symKeyOpt.map(symKey=>{




        })
      })





    Future{
      new Pyramid(pyramidConfig)
    }
  }

}
