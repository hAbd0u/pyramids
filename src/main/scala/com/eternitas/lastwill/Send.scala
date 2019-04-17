package com.eternitas.lastwill

import com.eternitas.lastwill.services.Web3Instance
import com.eternitas.wizard.JQueryWrapper
import org.scalajs.dom.raw.Event
import org.scalajs.jquery.JQuery

import scala.concurrent.ExecutionContext

object Send {


  implicit class PSend(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def onSend(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $ : JQueryWrapper,
                                     feedback: UserFeedback) = jq.click(
      (event:Event)=>{

        println("Send :" + Web3Instance.instance(eternitas))
      }
    )


  }

}
