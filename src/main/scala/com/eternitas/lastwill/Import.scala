package com.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQuery
import org.scalajs.dom.raw.{File, FileReader}
import com.eternitas.lastwill.Buffers._
import scala.concurrent.ExecutionContext

object Import {


  implicit class ImportJQuery(jq: JQuery) extends PimpedQuery.PJQuery(jq){


    def iimport(oldEternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $ : JQueryWrapper,
                                         feedback: UserFeedback) =
      onDrop(
        (file: File) =>
          new FileReader().onReadArrayBuffer(
            file,
            bufferSource =>
              if (file.`type` == "application/json")
                importFromData(oldEternitas, file, bufferSource)
              else
                feedback.error("Unsupported data type: " + file.`type`)
          )
      ).onDragOverNothing()
  }





}
