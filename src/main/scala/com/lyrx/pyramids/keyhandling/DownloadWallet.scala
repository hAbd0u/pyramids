package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.{Pyramid, PyramidConfig, PyramidJSON}
import org.scalajs.dom
import org.scalajs.dom.raw.{Blob, BlobPropertyBag}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import typings.jqueryLib.{JQuery}



@js.native
@JSGlobal
class MyWindow extends dom.Window {
  val URL: URL = js.native

}

@js.native
trait URL extends js.Any {

  def createObjectURL(blob: Blob): String = js.native

}

trait DownloadWallet extends PyramidJSON{




  val pyramidConfig: PyramidConfig



  val mywindow = js.Dynamic.global.window.asInstanceOf[MyWindow]


  def createObjectURL(blob: Blob): String = mywindow.URL.createObjectURL(blob)



  def downloadWallet[T](n:JQuery[T])(implicit executionContext: ExecutionContext):Future[JQuery[T]]=new Pyramid(pyramidConfig).
      exportAllKeys().
      map(walletNative=>stringify(walletNative)).
      map(s=>n.attr("href",createObjectURL(
        new Blob(js.Array(s), BlobPropertyBag("application/json")
        ))))




}
