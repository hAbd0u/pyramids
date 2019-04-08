package com.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.JQuery
import org.scalajs.dom.{Event, KeyboardEvent}

import scala.concurrent.ExecutionContext
object Loading {

  implicit class PLoading(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def cidEntered(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $ : JQueryWrapper,
                                         feedback: UserFeedback) = jq.
      on("keypress", (e: Event) => if (
        e.asInstanceOf[KeyboardEvent].keyCode == 13)
          handleCID(eternitas,jq.value.toString()))


    def handleCID(eternitas: Eternitas,s: String)(implicit ctx: ExecutionContext,
                             $ : JQueryWrapper,
                             feedback: UserFeedback) = {
      feedback.error("Loading your data, please be very patient ...")
      jq.value("")
      PimpedJQuery.
        loadHashAsText(s,b=>{
          LastWillStartup.init(eternitas.withPinDataHash(s))
        })
    }


    def unload(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $ : JQueryWrapper,
                                     feedback: UserFeedback) = jq.
      click((e:Event)=>
        LastWillStartup.init(eternitas.withoutPinDataHash()))







        /*
        val pinDataListNative:PinDataListNative = s.toDynamic()
        feedback.message(s"Resolved: ${pinDataListNative}")
        */



  }

}
