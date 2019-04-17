package com.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, File, FileReader}
import com.eternitas.lastwill.Buffers._
import org.scalajs.jquery.JQuery

import scala.concurrent.ExecutionContext
import scala.scalajs.js

object Import {


  implicit class ImportJQuery(jq: JQuery) extends PimpedJQuery.PJQuery(jq){

    def export(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $: JQueryWrapper,
                                     feedback: UserFeedback) = {
      eternitas
        .export()
        .onComplete(t =>
          if (t.isFailure) feedback.error("Export failed for keypair: " + t)
          else
            t.map((s: String) => {
              val blob: Blob =
                new Blob(js.Array(s), BlobPropertyBag("application/json"))
              val url = PimpedJQuery.createObjectURL(blob)
              jq.attr("href", url)
            }))
      jq

    }


    def iimport(oldEternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $ : JQueryWrapper,
                                         feedback: UserFeedback) =
      onDrop(
        (file: File) =>
          new FileReader().onReadArrayBuffer(
            file,
            bufferSource =>
              if (file.`type` == "application/json")
                importFromData( new Eternitas(ETConfig.empty()), file, bufferSource)
              else
                feedback.error("Unsupported data type: " + file.`type`)
          )
      ).onDragOverNothing()
  }





}
