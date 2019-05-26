package com.lyrx.pyramids.keyhandling

import org.scalajs.dom.File
import typings.jqueryLib.{JQuery, JQueryEventObject}

import scala.concurrent.Future

trait DragAndDrop {
  def onDrop[T](jq:JQuery[T],h: ((File) => Future[Any])): JQuery[T] = onDragOverNothing(jq.on(
    "drop",
    (evt: JQueryEventObject) => {
      evt.stopPropagation();
      evt.preventDefault();

      //println("Hiiiiier: "+ evt.originalEvent.asInstanceOf[DataTransferEvent].dataTransfer)
      evt.originalEvent.asInstanceOf[DataTransferEvent].
        dataTransfer.flatMap(dt=>dt.
        files.
        map(fs=>
          fs.
            headOption.
            map(blob=>h(blob.asInstanceOf[File]))))
      ()

    }
  ))

  def onDragOverNothing[T](jq:JQuery[T]): JQuery[T] = {
    jq.on("dragover",
      ( evt: JQueryEventObject) => {
        evt.stopPropagation();
        evt.preventDefault();
      })
  }


}
