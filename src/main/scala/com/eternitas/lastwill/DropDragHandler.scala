package com.eternitas.lastwill

import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom.raw.{Blob, File}

object DropDragHandler {

  implicit class PimpedJQuery(jquery:JQuery){

    def onDrop(h:(File=>Unit))=jquery.
      on("drop",(evt: JQueryEventObject) => {
      evt.
        stopPropagation();
      evt.preventDefault();
        evt.
        asInstanceOf[DataTransferEvent].
        originalEvent.
        map(_.dataTransfer.map(_.files)).
        map(_.map(_.map(
          _.headOption.
            map((blob)=> h(blob.asInstanceOf[File])))))
    })

}}
