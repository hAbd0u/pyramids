package com.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.JQuery
import org.scalajs.dom.{Event, KeyboardEvent}

import scala.concurrent.ExecutionContext

object Stampd {

  implicit class PStampd(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def stamp(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $ : JQueryWrapper,
                                         feedback: UserFeedback) = {}



  }

}
