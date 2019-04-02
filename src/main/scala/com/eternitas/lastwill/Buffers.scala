package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.HashSum.hash
import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import scala.scalajs.js.JSConverters._
import scala.util.Try


object Buffers {


  implicit class PimpedFileReader(f:FileReader){
    def onReadArrayBuffer(b:Blob, h:(ArrayBuffer) => Unit) = {
      f.readAsArrayBuffer(b)
      f.onloadend  = (e:ProgressEvent)=>h(f.result.asInstanceOf[ArrayBuffer])
    }


    def onReadUInt8Array(b:Blob, h:(ArrayBuffer) => Unit) = {

      f.onloadend  = (e:ProgressEvent)=>h(f.result.asInstanceOf[ArrayBuffer])
    }




    def onHash(blob:Blob,h:(String)=>Unit)(implicit ctx:ExecutionContext) =onReadArrayBuffer(blob, bufferSource=>{
      hash(bufferSource).
        onComplete((aTry:Try[ArrayBuffer])=>{
          aTry.map(ab=>h(ab.toHexString()))
        })
    })



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
