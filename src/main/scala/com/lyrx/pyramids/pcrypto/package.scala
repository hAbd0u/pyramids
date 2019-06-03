package com.lyrx.pyramids

import org.scalajs.dom.crypto
import org.scalajs.dom.crypto.BufferSource
import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js.typedarray.ArrayBuffer

package object pcrypto {








  def hash(b: BufferSource)(implicit excecutionContext:ExecutionContext): Future[ArrayBuffer] =
    crypto.crypto.subtle.digest("SHA-256", b).toFuture.map(_.asInstanceOf[ArrayBuffer])

}
