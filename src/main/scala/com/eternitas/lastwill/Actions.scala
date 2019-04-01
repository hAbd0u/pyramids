package com.eternitas.lastwill

import com.eternitas.lastwill.Buffers._

import com.eternitas.lastwill.axioss._
import com.eternitas.lastwill.cryptoo._
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom
import org.scalajs.dom.raw._
import scala.concurrent.ExecutionContext
import scala.scalajs.js

import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.{ArrayBuffer, ArrayBufferView, Uint8Array}
import scala.util.Try
@js.native
@JSGlobal
class MyWindow extends dom.Window {
  val URL: URL = js.native

}
@js.native
trait URL extends js.Any {

  def createObjectURL(blob: Blob): String = js.native

}

object Actions {

  implicit class PimpedJQuery(jq: JQuery) extends PimpedQuery.PJQuery(jq) {






    def showPinned(pinned: PinDataNative, eternitas: Eternitas)(
      implicit ctx: ExecutionContext,
      $: JQueryWrapper,
      feedback: UserFeedback) = {


      val el = $(
        s"<a href='#' download='${pinned.name.getOrElse("data.dat")}' " +
          s"  id='${pinned.hash.getOrElse(pinned.hashCode())}'  " +
          s"class='pinned'>${pinned.name.getOrElse("[UNNAMED]")}</a>")
      $("#data-display").append(el)
      el.click(
        (event: Event) => {
          event.preventDefault();
          event.stopPropagation()
          pinned.`hash`.map(aHash =>
            PimpedQuery.loadHashAsArrayBuffer(
              aHash,
              (encryptedData: ArrayBuffer) =>
                pinned.vc.map(avchash =>
                  PimpedQuery.loadHashAsArrayBuffer(
                    avchash,
                    (vc: ArrayBuffer) =>
                      eternitas.keyOpt.map(symKey => {
                        //feedback.log("Data to decrypt", encryptedData)
                        //feedback.log("IV for decrypt", vc)
                        val f = SymCrypto.decrypt(symKey, encryptedData, vc)
                        f.map((t: ArrayBuffer) => {
                          feedback.message("Decryption successfull")
                          val blob: Blob =
                            new Blob(js.Array[js.Any](t),
                              BlobPropertyBag(pinned.`type`.getOrElse("octet/stream").toString))
                          val url = PimpedQuery.createObjectURL(blob)
                          dom.window.open(url)
                        })
                        f.failed.map(e =>
                          feedback.error(
                            s"Decryption failed: ${e.getLocalizedMessage}"))
                      })
                  ))
            ))
        }
      )
    }

    def dataDisplay(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                          $: JQueryWrapper,
                                          feedback: UserFeedback) = {

      eternitas.pinDataOpt.map(
        aHash =>
          PimpedQuery.loadHashAsText(
            aHash,
            (data: String) =>
              js.JSON
                .parse(data.toString)
                .asInstanceOf[PinDataListNative]
                .data
                .map(realData =>
                  realData.foreach(pinned => showPinned(pinned, eternitas)))
          ))
    }





  }

}

