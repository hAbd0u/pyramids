package com.eternitas.lastwill

import com.eternitas.lastwill.cryptoo.{AsymCrypto, SymCrypto, WalletNative}
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom.raw._

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import com.eternitas.lastwill.Buffers._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSGlobal


@js.native
@JSGlobal
class MyWindow extends dom.Window {
  val URL: URL = js.native

}
@js.native
trait URL extends js.Any {

  def createObjectURL(blob: Blob): String = js.native

}

object PimpedJQuery {

  val mywindow = js.Dynamic.global.window.asInstanceOf[MyWindow]


  def createObjectURL(blob: Blob): String = mywindow.URL.createObjectURL(blob)


  def resolveUrl(aHash:String) = {
  //println(s"Host: ${mywindow.location.host}")
    if (
      mywindow.location.host.matches(".*localhost.*"))
      s"https://ipfs.io/ipfs/${aHash}"
    else
      s"/ipfs/${aHash}"
  }

  def currentHash() = {
    val aHash = mywindow.location.hash
    val index =aHash.indexOf("#")
    if( index >= 0) Some(aHash.substring(index+1))  else  None
  }



  def loadHashAsArrayBuffer(aHash: String, cb: (ArrayBuffer) => Unit)(
    implicit
    $: JQueryWrapper) = {
    val r = new XMLHttpRequest()
    r.open("GET", resolveUrl(aHash), true);
    r.responseType = "arraybuffer"
    r.onload = (oEvent: Event) => cb(r.response.asInstanceOf[ArrayBuffer]);
    r.send()
  }

  def loadHashAsText(aHash: String, cb: (String) => Unit)(implicit
                                                          $: JQueryWrapper) = {
    val r = new XMLHttpRequest()
    r.open("GET", resolveUrl(aHash), true);
    r.responseType = "text"
    r.onload = (oEvent: Event) => cb(r.response.toString());
    r.send()
  }



  implicit class PJQuery(jq: JQuery) {

    def onDrop(h: (File => Unit)): JQuery =
      jq.on(
        "drop",
        (evt: JQueryEventObject) => {
          evt.stopPropagation();
          evt.preventDefault();
          evt
            .asInstanceOf[DataTransferEvent]
            .originalEvent
            .map(_.dataTransfer.map(_.files))
            .map(_.map(_.map(_.headOption.map((blob) =>
              h(blob.asInstanceOf[File])))))
        }
      )

    def onDragOverNothing(): JQuery = {
      jq.on("dragover",
        (e: Element, evt: JQueryEventObject, t2: Any, t3: Any) => {
          evt.stopPropagation();
          evt.preventDefault();
        })

    }


     def importFromData(oldEternitas: Eternitas,
                               file: File,
                               bufferSource: ArrayBuffer)(
                                implicit $: JQueryWrapper,
                                feedback: UserFeedback,
                                executionContext: ExecutionContext) = {
      val walletNative: WalletNative =
        js.JSON.parse(bufferSource.toNormalString()).asInstanceOf[WalletNative]
      val et1 = oldEternitas.withPinData(walletNative.pinfolder)
      AsymCrypto.importKeyPair(file,
        et1,
        walletNative,
        (et2: Eternitas) => onImportKeyPair(file, walletNative, et2))
    }

    def onImportKeyPair(file: File, walletNative: WalletNative, et2: Eternitas)(
      implicit $: JQueryWrapper,
      feedback: UserFeedback,
      executionContext: ExecutionContext): Unit = SymCrypto.importKey(
      et2,
      walletNative,
      (et3) => LastWillStartup.init(AsymCrypto.importPinata(et3, walletNative))
    )


  }

}
