package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.File

import scala.concurrent.{ExecutionContext, Future}

trait Encryption extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def symEncrypt(file:File)(implicit ctx:ExecutionContext):Future[Pyramid]

}
