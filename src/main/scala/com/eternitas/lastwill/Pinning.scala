package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.{PinDataListNative, PinDataNative, SymCrypto}
import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQuery
import org.scalajs.dom
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, Event}

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

object Pinning {

  implicit class PPinning(jq: JQuery) extends PimpedQuery.PJQuery(jq){




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
