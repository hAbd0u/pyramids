package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.File

import scala.concurrent.{ExecutionContext, Future}

trait Decryption  extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def symDecrypt()(implicit ctx:ExecutionContext):Future[Pyramid] =  pyramidConfig.
    symKeyOpt.map(symKey=> decrypt(symKey,pyramidConfig.distributedData)).
    map(_.map(d=>new Pyramid(
      pyramidConfig.copy(distributedData = d).
        msg("Your data has been decryted, oh Pharao!")
    ))).
    getOrElse(Future{new Pyramid(pyramidConfig.msg("Oh pharao, we have not found your decryption key!"))})


}
