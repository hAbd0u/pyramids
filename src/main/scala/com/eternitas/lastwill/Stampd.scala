package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.AsymCrypto
import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQuery
import org.scalajs.dom.Event
import org.scalajs.dom.raw.File
import Buffers._;
import scala.concurrent.ExecutionContext

object Stampd {

  implicit class PStampd(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def stamp(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $ : JQueryWrapper,
                                         feedback: UserFeedback) = {
      jq.click(
        (e:Event) => {
          e.preventDefault()
          e.stopPropagation()


          eternitas.config.pinDataOpt.map(pinData=>eternitas.
            config.
            signKeyPair.
          keyPairOpt.
          map(kp=>AsymCrypto.sign(kp,pinData.toArrayBuffer()).
          onComplete(t=>println("Sign: " + t))))
        }
      )

      onDrop( (f:File)=>{
        println("TODO: Implement dop")
      }).onDragOverNothing()
    }



  }

}
