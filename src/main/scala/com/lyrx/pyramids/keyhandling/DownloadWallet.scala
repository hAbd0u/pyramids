package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.{Pyramid, PyramidConfig, PyramidJSON}
import org.scalajs.dom
import org.scalajs.dom.raw//.{Blob, BlobPropertyBag}
import typings.stdLib
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import typings.fileDashSaverLib.fileDashSaverMod.{^ => filesaver}


@js.native
@JSGlobal
class MyWindow extends dom.Window {
  val URL: URL = js.native

}

@js.native
trait URL extends js.Any {

  def createObjectURL(blob: raw.Blob): String = js.native

}

trait DownloadWallet extends PyramidJSON{




  val pyramidConfig: PyramidConfig



  val mywindow = js.Dynamic.global.window.asInstanceOf[MyWindow]


  def createObjectURL(blob: raw.Blob): String = mywindow.URL.createObjectURL(blob)



  def downloadWallet()
                       (implicit executionContext: ExecutionContext)= new
      Pyramid(pyramidConfig).
      exportAllKeys().
      map(walletNative =>filesaver.
        saveAs(
          new raw.Blob(
            js.Array(stringify(walletNative)),
            raw.BlobPropertyBag("application/json")).
            asInstanceOf[stdLib.Blob],
        "pyramid-keys.json")).
      map(v=>pyramidConfig.
          msg(s"Oh ${pyramidConfig.name()}, you have saved your keys!"))







}
