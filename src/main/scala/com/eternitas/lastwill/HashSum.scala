package com.eternitas.lastwill

import org.scalajs.dom.crypto.BufferSource
import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.scalajs.js
import org.scalajs.dom.crypto._

import scala.concurrent.ExecutionContext
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}

object HashSum{
 implicit class PimpedFileReader(f:FileReader){
    def onRead(b:Blob, h:(BufferSource) => Unit) = {
       f.readAsArrayBuffer(b)
      f.onloadend  = (e:ProgressEvent)=>h(f.result.asInstanceOf[BufferSource])
    }

   implicit class PimpedArrayBuffer(b:ArrayBuffer){

     def toHexString() = new Uint8Array(b).
       map(c=>c.toHexString).foldLeft("")((a:String,b:String)=>a + b)
   }



    def onHash(blob:Blob,h:(String)=>Unit) ={
      implicit  val executionContext= ExecutionContext.global
      onRead(blob,bufferSource=>{
        hash(bufferSource).
          toFuture.
          onComplete(_.map(aAny=>h(aAny.asInstanceOf[ArrayBuffer].toHexString())))
      })
    }


  }




  def hash(b:BufferSource):js.Promise[js.Any] = crypto.subtle.digest("SHA-256",b)

}




@js.native
trait Binary extends js.Object {

}

@js.native
trait  HashArrayBuffer extends js.Object {


  def hash(b:Binary):String = js.native


}
