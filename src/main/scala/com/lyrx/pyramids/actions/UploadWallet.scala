package com.lyrx.pyramids.actions

import com.lyrx.pyramids.PyramidConfig
import org.scalajs.dom.File
import org.scalajs.dom.raw.{Blob, EventTarget}
import org.scalajs.jquery.{JQuery, JQueryEventObject}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.UndefOr


@js.native
trait DataTransferTarget extends EventTarget {


  val files:js.UndefOr[js.Array[Blob]] = js.native

}


@js.native
trait DataTransferEvent extends JQueryEventObject {

  val dataTransfer:UndefOr[DataTransferTarget] = js.native

  val originalEvent:UndefOr[DataTransferEvent]=js.native

}


trait UploadWallet {

  val pyramidConfig:PyramidConfig




  def uploadWallet(f:File)(implicit executionContext: ExecutionContext):Future[PyramidConfig] = {

    println(s"Dropped: ${f.name}")

    Future {pyramidConfig}

  }


}
