package com.eternitas.lastwill

import com.eternitas.lastwill.Buffers._
import com.eternitas.lastwill.axioss.{AxiosImpl, Pinata, PinataMetaData, PinataPinResponse}
import com.eternitas.lastwill.cryptoo.AsymCrypto
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.JQuery
import org.scalajs.dom.Event
import org.scalajs.dom.raw.File

import scala.concurrent.ExecutionContext
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.scalajs.js.Dynamic.{literal => l}

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
            auth=>pinSignature(eternitas, b, auth).
              `then`(r=>onPinnedSignature(
                r.asInstanceOf[PinataPinResponse],
                pinData,
                auth,
                eternitas.
                  config.
                  signKeyPair.
                  nameOpt,
                eternitas
              )).
              `catch`(e=>feedback.error(e.toString))
          )))))
        }
      )

      onDrop( (f:File)=>{
        println("TODO: Implement dop")
      }).onDragOverNothing()
    }


    def onPinnedSignature(
                           r: PinataPinResponse,
                           pinData:String,
                           auth:AllCredentials,
                           nameOpt:Option[String],
                           eternitas: Eternitas
                         )(implicit ctx: ExecutionContext,
                           $ : JQueryWrapper,
                           feedback: UserFeedback): Unit = {

      new Pinata(auth).pinFileToIPFS(
      Eternitas.stringify(l(
        "signature" -> r.data.IpfsHash,
        "data" -> pinData
      )).toArrayBuffer(),
        nameOpt.map(name=>PinataMetaData(Some(s"SIGNATURE-MAPPING ${name}"),None,None)).
          getOrElse(PinataMetaData(Some(s"SIGNATURE-MAPPING"),None,None))).
        `then`(r=>{

          LastWillStartup.init(eternitas.withSignature(r.asInstanceOf[PinataPinResponse].data.IpfsHash))
        }).
      `catch`(e=>feedback.error(e.toString()))

      //userFeedback.message(r.data.IpfsHash)
    }

    def pinSignature(eternitas: Eternitas, b: ArrayBuffer, auth: AllCredentials): AxiosImpl = {
      new Pinata(auth).pinFileToIPFS(
        b,
        eternitas.
          config.
          signKeyPair.
          nameOpt.
          map(name =>
            PinataMetaData(Some("SIGNATURE from " + name), None, None)).
          getOrElse(PinataMetaData(Some("SIGNATURE"), None, None)))
    }
  }

}
