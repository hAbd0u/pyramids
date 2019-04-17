package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.{PinDataListNative, PinDataNative, SymCrypto}
import com.eternitas.wizard.JQueryWrapper
import org.scalajs.dom
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, Event}
import org.scalajs.jquery.JQuery

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

object Pinning {

  implicit class PPinning(jq: JQuery) extends PimpedJQuery.PJQuery(jq){




    def showPinned(pinned: PinDataNative, eternitas: Eternitas)(
      implicit ctx: ExecutionContext,
      $: JQueryWrapper,
      feedback: UserFeedback) = {


      val el = $(
        s"<a href='#' download='${pinned.hash.getOrElse("data.dat")}' " +
          s"  id='${pinned.hash.getOrElse(pinned.hashCode())}'  " +
          s"class='pinned'>${pinned.hash.getOrElse("[UNNAMED]")}</a> <span>&nbsp; | &nbsp; </span>")
      $("#data-display").append(el)
      el.click(
        (event: Event) => {
          event.preventDefault();
          event.stopPropagation()
          pinned.`hash`.map(aHash =>
            PimpedJQuery.loadHashAsArrayBuffer(
              aHash,
              (encryptedData: ArrayBuffer) =>
                pinned.vc.map(avchash =>
                  PimpedJQuery.loadHashAsArrayBuffer(
                    avchash,
                    (vc: ArrayBuffer) =>
                      eternitas.config.keyOpt.map(symKey => {
                        //feedback.log("Data to decrypt", encryptedData)
                        //feedback.log("IV for decrypt", vc)
                        val f = SymCrypto.decrypt(symKey, encryptedData, vc)
                        f.map((t: ArrayBuffer) => {
                          val blob: Blob =
                            new Blob(js.Array[js.Any](t),
                              BlobPropertyBag(pinned.`type`.getOrElse("octet/stream").toString))
                          val url = PimpedJQuery.createObjectURL(blob)
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

      eternitas.config.pinDataOpt.map(
        aHash => if(aHash.length > 0)
          PimpedJQuery.loadHashAsText(
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
