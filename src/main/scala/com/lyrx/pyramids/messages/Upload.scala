package com.lyrx.pyramids.messages

import org.scalajs.dom.raw.{Event, File, FileReader}
import org.scalajs.jquery.JQuery

import scala.concurrent.ExecutionContext


object Upload {

  implicit class PUpload(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def upLoad(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $ : JQueryWrapper,
                                     feedback: UserFeedback) = {
      onDrop(
        (file: File) =>
          handleDrop(eternitas, file)
      ).onDragOverNothing()

      jq.click((e:Event)=>{
        println("TODO: Implement  click in Drob Zone!" )

      })
    }

}
