package com.eternitas.lastwill

import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.scalajs.js




object HashSum{
 implicit class PimpedFileReader(f:FileReader){
    def onRead(b:Blob, h:(Binary) => Unit) = {
       f.readAsArrayBuffer(b).asInstanceOf[Binary]
      f.onloadend  = (e:ProgressEvent)=>h(f.result.asInstanceOf[Binary])
    }


    def onHash(blob:Blob,h:(String)=>Unit) =
      onRead(blob,binary=>h(hash(binary)))

  }




  def hash(b:Binary) = {
    //org.scalajs.dom.crypto.crypto.subtle.digest()
    "Not implemented"
    /*
    js.Dynamic.global.
      SparkMD5.ArrayBuffer.asInstanceOf[Md5ArrayBuffer].hash(b)
      */
  }
}




@js.native
trait Binary extends js.Object {

}

@js.native
trait  HashArrayBuffer extends js.Object {


  def hash(b:Binary):String = js.native


}
