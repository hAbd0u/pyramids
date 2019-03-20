package com.eternitas.lastwill

import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.scalajs.js




object MD5Sum{
 implicit class PimpedFileReader(f:FileReader){
    def onRead(b:Blob, h:(Binary) => Unit) = {
      val r = f.readAsArrayBuffer(b).asInstanceOf[Binary]
      f.onloadend  = (e:ProgressEvent)=>h(r)
    }


    def onHash(blob:Blob,h:(String)=>Unit) =
      onRead(blob,binary=>h(hash(binary)))

  }




  def hash(b:Binary) = js.Dynamic.global.
    SparkMD5.ArrayBuffer.asInstanceOf[Md5ArrayBuffer].hash(b)
}




@js.native
trait Binary extends js.Object {

}

@js.native
trait  Md5ArrayBuffer extends js.Object {


  def hash(b:Binary):String = js.native


}
