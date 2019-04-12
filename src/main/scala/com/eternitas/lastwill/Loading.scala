package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.{AsymCrypto, SignatureNative}
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.JQuery
import org.scalajs.dom.{Event, KeyboardEvent, crypto}
import com.eternitas.lastwill.Buffers._
import org.scalajs.dom.crypto.JsonWebKey

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
object Loading {

  implicit class PLoading(jq: JQuery) extends PimpedJQuery.PJQuery(jq) {

    def cidEntered(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $: JQueryWrapper,
                                         feedback: UserFeedback) = jq.
      on("keypress", (e: Event) => if (
        e.asInstanceOf[KeyboardEvent].keyCode == 13)
        handleCID(eternitas, jq.value.toString()))


    def handleCID(eternitas: Eternitas, s: String)(implicit ctx: ExecutionContext,
                                                   $: JQueryWrapper,
                                                   feedback: UserFeedback) = {
      feedback.error("Loading your data, please be very patient ...")
      jq.value("")
      PimpedJQuery.
        loadHashAsText(s, b => {
          LastWillStartup.init(eternitas.withPinDataHash(s))
        })
    }


    def unload(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $: JQueryWrapper,
                                     feedback: UserFeedback) = jq.
      click((e: Event) => {
        e.preventDefault()
        e.stopPropagation()
        LastWillStartup.init(eternitas.withoutPinDataHash())
      })


    def signedHash(eternitas: Eternitas, aHash: String)(implicit ctx: ExecutionContext,
                                                    $: JQueryWrapper,
                                                    feedback: UserFeedback) = {
      jq.value("")
      PimpedJQuery.
      loadHashAsText(aHash, b =>{
        val signNative = js.JSON.parse(b).asInstanceOf[SignatureNative]
         signNative.
           signature.
           map(signatureHash=>
             PimpedJQuery.
               loadHashAsArrayBuffer(signatureHash,
                 (signatureData:ArrayBuffer)=>{
                   signNative.verify.map(
                     verifyJSKey=>
                       AsymCrypto.importVerifyKey(verifyJSKey).
                         onComplete(t=>t.map(verifyKey=>{
                           signNative.data.map(dataHash=>{
                             AsymCrypto.
                               verify(
                                 verifyKey,
                                 signatureData,
                                 dataHash.toArrayBuffer()).
                               onComplete(t=>t.map(b=>
                                 if(b)
                                   signNative.
                                     data.
                                     map(pinHash=>
                                       LastWillStartup.
                                         init(
                                           eternitas.
                                             withPinDataHash(pinHash)))
                                 else
                                   feedback.error(s"Verification failed!")
                               ))
                           })
                         })))
                 }))

      } )
  }



    def handleSigned(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                                      $ : JQueryWrapper,
                                                      feedback: UserFeedback) = {
      jq.
        on("keypress", (e: Event) => if (
          e.asInstanceOf[KeyboardEvent].keyCode == 13)
          signedHash(eternitas,jq.value.toString()))
    }






        /*
        val pinDataListNative:PinDataListNative = s.toDynamic()
        feedback.message(s"Resolved: ${pinDataListNative}")
        */



  }

}
