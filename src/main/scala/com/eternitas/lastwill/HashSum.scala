package com.eternitas.lastwill

import org.scalajs.dom.crypto.BufferSource
import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.scalajs.js
import org.scalajs.dom.crypto._

import scala.concurrent.ExecutionContext
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import scala.util.Try

object HashSum{
 implicit class PimpedFileReader(f:FileReader){
    def onRead(b:Blob, h:(BufferSource) => Unit) = {
       f.readAsArrayBuffer(b)
      f.onloadend  = (e:ProgressEvent)=>h(f.result.asInstanceOf[BufferSource])
    }




    def onHash(blob:Blob,h:(String)=>Unit)(implicit ctx:ExecutionContext) =onRead(blob,bufferSource=>{
        hash(bufferSource).
          toFuture.
          onComplete((aTry:Try[Any])=>{
            aTry.map(aAny=>h(aAny.asInstanceOf[ArrayBuffer].toHexString()))
          })
      })



  }

  implicit class PimpedArrayBuffer(b:ArrayBuffer){

    def toHexString() = new Uint8Array(b).
      map(c=>c.toHexString).foldLeft("")((a:String,b:String)=>a + b)

    def toNormalString()=new Uint8Array(b).
      map((c:Short)=>c.toChar).foldLeft(s"-${b.byteLength}":String)((a:String,b:Char)=>a + b)
  }



  def hash(b:BufferSource):js.Promise[js.Any] = crypto.subtle.digest("SHA-256",b)

}



