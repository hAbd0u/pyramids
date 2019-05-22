package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.PyramidConfig
import org.scalajs.dom.File
import org.scalajs.dom.raw.{Blob, EventTarget, FileReader}
import org.scalajs.jquery.{JQuery, JQueryEventObject}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.UndefOr
import scala.scalajs.js.typedarray.ArrayBuffer


@js.native
trait DataTransferTarget extends EventTarget {


  val files:js.UndefOr[js.Array[Blob]] = js.native

}


@js.native
trait DataTransferEvent extends JQueryEventObject {

  val dataTransfer:UndefOr[DataTransferTarget] = js.native

  val originalEvent:UndefOr[DataTransferEvent]=js.native

}


trait UploadWallet extends KeyImport {






  def uploadWallet(f:File)(implicit executionContext: ExecutionContext) = {

    //if (f.`type` == "application/json")

      new FileReader().futureReadArrayBuffer(f).
        map(arrayBuffer =>js.JSON.parse(arrayBuffer).asInstanceOf[WalletNative])

    //else
      //Future{None}





  }


}
