package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.PyramidConfig
import org.scalajs.dom.File
import org.scalajs.jquery.{JQuery, JQueryEventObject}

import scala.concurrent.Future

trait DragAndDrop {
  def onDrop(jq:JQuery,h: ((File) => Future[Any])): JQuery = onDragOverNothing(jq.on(
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
  ))

  def onDragOverNothing(jq:JQuery): JQuery = {
    jq.on("dragover",
      ( evt: JQueryEventObject) => {
        evt.stopPropagation();
        evt.preventDefault();
      })
  }


}
