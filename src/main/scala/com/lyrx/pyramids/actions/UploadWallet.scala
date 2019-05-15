package com.lyrx.pyramids.actions

import org.scalajs.dom.File
import org.scalajs.dom.raw.{Blob, EventTarget}
import org.scalajs.jquery.{JQuery, JQueryEventObject}

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

  def onDrop(jq:JQuery,h: (File => Unit)): JQuery = jq.on(
      "drop",
      (evt: JQueryEventObject) => {
        evt.stopPropagation();
        evt.preventDefault();
        evt
          .asInstanceOf[DataTransferEvent]
          .originalEvent
          .map(_.dataTransfer.map(_.files))
          .map(_.map(_.map(_.headOption.map((blob) =>
            h(blob.asInstanceOf[File])))))
      }
    )



}
