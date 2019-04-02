package com.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQuery
import org.scalajs.dom.raw.Event

import scala.concurrent.ExecutionContext

object Loading {

  implicit class PLoading(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def cidEntered(eternitas:Eternitas)(implicit ctx: ExecutionContext,
                     $ : JQueryWrapper,
                     feedback: UserFeedback) {
      jq.on("", (e: Event) => {
        feedback.message("This is it!")
      })
    }

  }

}
