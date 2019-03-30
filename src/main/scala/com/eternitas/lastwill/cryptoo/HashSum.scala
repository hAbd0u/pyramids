package com.eternitas.lastwill.cryptoo



import org.scalajs.dom.crypto
import org.scalajs.dom.crypto.BufferSource

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

object HashSum {

  def hash(b: BufferSource)(implicit excecutionContext:ExecutionContext): Future[ArrayBuffer] =
    crypto.crypto.subtle.digest("SHA-256", b).toFuture.map(_.asInstanceOf[ArrayBuffer])

}
