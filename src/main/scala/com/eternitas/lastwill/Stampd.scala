package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.AsymCrypto
import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQuery
import org.scalajs.dom.Event
import org.scalajs.dom.raw.File
import Buffers._
import com.eternitas.lastwill.axioss.{Pinata, PinataMetaData, PinataPinResponse}

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
          onComplete(t=>t.map(b=>eternitas.config.allAuth.map(
            auth=>new Pinata(auth).pinFileToIPFS(
              b,
              PinataMetaData(Some("SIGNATURE"),None,None)).
              `then`(r=>feedback.message(r.asInstanceOf[PinataPinResponse].data.IpfsHash)).
              `catch`(e=>feedback.error(e.toString))
          )))))
        }
      )

      onDrop( (f:File)=>{
        println("TODO: Implement dop")
      }).onDragOverNothing()
    }



  }

}
