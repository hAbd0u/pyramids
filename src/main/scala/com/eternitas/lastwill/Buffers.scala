package com.eternitas.lastwill

import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import scala.scalajs.js.JSConverters._


object Buffers {


  implicit class PimpedString(s:String){
    def toUInt8Array():Uint8Array = new Uint8Array(s.getBytes().toJSArray)

    def toArrayBuffer() = toUInt8Array().buffer
  }

  implicit class PimpedArrayBuffer(b:ArrayBuffer){

    def toHexString() = new Uint8Array(b).
      map(c=>c.toHexString).foldLeft("")((a:String,b:String)=>a + b)

    def toNormalString()=new Uint8Array(b).
      map((c:Short)=>c.toChar).foldLeft(s"":String)((a:String,b:Char)=>a + b)
  }


}
