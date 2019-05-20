package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import org.scalajs.dom.File

import scala.concurrent.{ExecutionContext, Future}

trait Decryption  extends  SymetricCrypto  {

  val pyramidConfig: PyramidConfig

  def symDecrypt(file:File)(implicit ctx:ExecutionContext):Future[Pyramid]

}
