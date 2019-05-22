package com.lyrx.pyramids

import org.scalajs.dom.crypto
import org.scalajs.dom.crypto.BufferSource
import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import scala.util.Try
import scala.scalajs.js.JSConverters._

package object cryptography {


  def hash(b: BufferSource)(implicit excecutionContext:ExecutionContext): Future[ArrayBuffer] =
    crypto.crypto.subtle.digest("SHA-256", b).toFuture.map(_.asInstanceOf[ArrayBuffer])

  implicit class PimpedFileReader(f:FileReader){


    def futureReadArrayBuffer(b:Blob) = {
      val promise = Promise[ArrayBuffer]
      f.readAsArrayBuffer(b)
      f.onloadend  = (e:ProgressEvent) => promise.success(f.result.asInstanceOf[ArrayBuffer)
      f.onerror = (e) => promise.failure(new Throwable(e.toString))
      f.onabort = (e) => promise.failure(new Throwable(e.toString))
      promise.future
    }









  }


  implicit class PimpedString(s:String){
    def toUInt8Array():Uint8Array = new Uint8Array(s.getBytes().toJSArray)

    def toArrayBuffer() = toUInt8Array().buffer

    def toDynamic[T <: js.Dynamic]():T = js.JSON.parse(s).asInstanceOf[T]
  }

  implicit class PimpedArrayBuffer(b:ArrayBuffer){



    def toHexString() = new Uint8Array(b).
      map(c=>c.toHexString).foldLeft("")((a:String,b:String)=>a + b)

    def toNormalString()=new Uint8Array(b).
      map((c:Short)=>c.toChar).foldLeft(s"":String)((a:String,b:Char)=>a + b)
  }




}
