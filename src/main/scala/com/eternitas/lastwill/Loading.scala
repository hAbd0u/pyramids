package com.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQuery
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.Event
import scala.concurrent.ExecutionContext

object Loading {

  implicit class PLoading(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def cidEntered(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $ : JQueryWrapper,
                                         feedback: UserFeedback) =
      jq.on("keypress", (e: Event) => {

        val textEvent = e.asInstanceOf[KeyboardEvent]

        if (textEvent.keyCode == 13)
          handleCID(jq.value.toString())
      })

    def handleCID(s: String)(implicit ctx: ExecutionContext,
                             $ : JQueryWrapper,
                             feedback: UserFeedback) = {
      feedback.message(s"${}")
    }
  }

}
