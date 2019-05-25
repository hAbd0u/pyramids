package com.lyrx.pyramids

import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.concurrent.Promise
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}

package object keyhandling {




  implicit class PimpedFileReader(f:FileReader){


    def futureReadArrayBuffer(b:Blob) = {
      val promise = Promise[ArrayBuffer]
      f.readAsArrayBuffer(b)
      f.onloadend  = (e:ProgressEvent) => promise.success(f.result.asInstanceOf[ArrayBuffer])
      f.onerror = (e) => promise.failure(new Throwable(e.toString))
      f.onabort = (e) => promise.failure(new Throwable(e.toString))
      promise.future
    }









  }



  implicit class PimpedArrayBuffer(b:ArrayBuffer){

    def toHexString() = new Uint8Array(b).
      map(c=>c.toHexString).foldLeft("")((a:String,b:String)=>a + b)

    def toNormalString()=new Uint8Array(b).
      map((c:Short)=>c.toChar).foldLeft(s"":String)((a:String,b:Char)=>a + b)
  }




}
