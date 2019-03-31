package com.eternitas.lastwill

import com.eternitas.lastwill.Buffers._
import com.eternitas.lastwill.PinData.PinningData
import com.eternitas.lastwill.axioss._
import com.eternitas.lastwill.cryptoo.{
  AsymCrypto,
  SymCrypto,
  SymEncryptionResult,
  WalletNative
}
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
  val mywindow = js.Dynamic.global.window.asInstanceOf[MyWindow]

  implicit class PimpedJQuery(jq: JQuery) {

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

    def export(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $ : JQueryWrapper,
                                     feedback: UserFeedback) =
      jq.click(
        (e: Event) =>
          eternitas
            .export()
            .onComplete(t =>
              if (t.isFailure) feedback.error("Export failed for keypair: " + t)
              else
                t.map((s: String) => {
                  val blob: Blob =
                    new Blob(js.Array(s), BlobPropertyBag("octet/stream"))
                  val url: String = mywindow.URL.createObjectURL(blob)
                  dom.window.location.assign(url)
                })))

    def upLoad(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $ : JQueryWrapper,
                                     feedback: UserFeedback) =
      onDrop(
        (file: File) =>
          eternitas.keyOpt.map(
            key =>
              new FileReader().onReadArrayBuffer(
                file,
                (arrayBuffer: ArrayBuffer) =>
                  SymCrypto
                    .encrypt(key, arrayBuffer)
                    .onComplete((t: Try[SymEncryptionResult]) =>
                      onEncryptionResult(t, eternitas, file))
            ))
      ).onDragOverNothing()

    def loadHashAsArrayBuffer(aHash: String, cb: (ArrayBuffer) => Unit)(
        implicit
        $ : JQueryWrapper) = {
      val r = new XMLHttpRequest()
      r.open("GET", s"/ipfs/${aHash}", true);
      r.responseType = "arraybuffer"
      r.onload = (oEvent: Event) => cb(r.response.asInstanceOf[ArrayBuffer]);
      r.send()
    }
    def loadHashAsText(aHash: String, cb: (String) => Unit)(
        implicit
        $ : JQueryWrapper) = {
      val r = new XMLHttpRequest()
      r.open("GET", s"/ipfs/${aHash}", true);
      r.responseType = "text"
      r.onload = (oEvent: Event) => cb(r.response.toString());
      r.send()
    }

    def showPinned(pinned: PinData.Pinned, eternitas: Eternitas)(
        implicit ctx: ExecutionContext,
        $ : JQueryWrapper,
        feedback: UserFeedback) = {
      val el = $(
        s"<a href='#' class='pinned'>${pinned.name.getOrElse("[UNNAMED]")}</a>")
      $("#data-display").empty().append(el)
      el.click(
        (event: Event) =>
          pinned.`hash`.map(aHash =>
            loadHashAsArrayBuffer(
              aHash,
              (encryptedData: ArrayBuffer) =>
                pinned.vc.map(avchash =>
                  loadHashAsArrayBuffer(
                    avchash,
                    (vc: ArrayBuffer) =>
                      eternitas.keyOpt.map(symKey => {
                        feedback.log("Data to decrypt", encryptedData)
                        feedback.log("IV for decrypt", vc)
                        val f = SymCrypto.decrypt(symKey, encryptedData, vc)
                        f.map((t: ArrayBuffer) =>
                          feedback.message("Decryption successfull"))
                        f.failed.map(e =>
                          feedback.error(
                            s"Decryption failed: ${e.getLocalizedMessage}"))
                      })
                ))
          )))
    }

    def dataDisplay(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                          $ : JQueryWrapper,
                                          feedback: UserFeedback) =
      eternitas.pinDataOpt.map(
        aHash =>
          loadHashAsText(aHash,
                         (data: String) =>
                           js.JSON
                             .parse(data.toString)
                             .asInstanceOf[PinningData]
                             .data
                             .foreach(pinned => showPinned(pinned, eternitas))))

    def iimport(oldEternitas: Eternitas)(implicit ctx: ExecutionContext,
                                         $ : JQueryWrapper,
                                         feedback: UserFeedback) =
      onDrop(
        (file: File) =>
          new FileReader().onReadArrayBuffer(
            file,
            bufferSource =>
              if (file.`type` == "application/json")
                importFromData(oldEternitas, file, bufferSource)
              else
                feedback.error("Unsupported data type: " + file.`type`)
        )
      ).onDragOverNothing()

  }

  private def onEncryptionResult(t: Try[SymEncryptionResult],
                                 eternitas: Eternitas,
                                 file: File)(
      implicit feedback: UserFeedback,
      $ : JQueryWrapper,
      executionContext: ExecutionContext) = {

    t.failed.map(thr =>
      feedback.error(s"Encryption failed: ${thr.getMessage()}"))
    t.map((symEncryptionResult: SymEncryptionResult) => {
      feedback.log(s"Encrypted", symEncryptionResult.result)
      feedback.log(s"IV", symEncryptionResult.iv)
      eternitas.pinnataOpt.map(p =>
        withPinata(eternitas, file, symEncryptionResult, p))
    })

  }

  def withPinata(eternitas: Eternitas,
                 file: File,
                 symEncryptionResult: SymEncryptionResult,
                 p: PinataAuth)(implicit feedback: UserFeedback,
                                executionContext: ExecutionContext,
                                $ : JQueryWrapper): AxiosImpl = {

    feedback.message("Start pinning, please be very patient!")
    new Pinata(p)
      .pinFileToIPFS(symEncryptionResult.result, PinataMetaData(file))
      .`then`((axiosResponse) =>
        pinataUpload(eternitas, symEncryptionResult, p, axiosResponse))
      .`catch`((error) => feedback.message(s"Error pinning hash: ${error}"))

  }

  def pinataUpload(eternitas: Eternitas,
                   symEncryptionResult: SymEncryptionResult,
                   p: PinataAuth,
                   axiosResponse: AxiosResponse)(
      implicit $ : JQueryWrapper,
      feedback: UserFeedback): AxiosImpl = {

    new Pinata(p)
      .pinFileToIPFS(symEncryptionResult.iv,
                     PinataMetaData(
                       axiosResponse
                         .asInstanceOf[PinataPinResponse]
                         .data))
      .`then`((axiosResponse2) =>
        handlePinResult(eternitas, axiosResponse, axiosResponse2))
      .`catch`((error) => feedback.message(s"Error pinning iv: ${error}"))

  }

  private def importFromData(oldEternitas: Eternitas,
                             file: File,
                             bufferSource: ArrayBuffer)(
      implicit $ : JQueryWrapper,
      feedback: UserFeedback,
      executionContext: ExecutionContext) = {
    val walletNative: WalletNative =
      js.JSON.parse(bufferSource.toNormalString()).asInstanceOf[WalletNative]
    val et1 = oldEternitas.withPinData(walletNative.pinfolder)
    AsymCrypto.importKeyPair(
      et1,
      walletNative,
      (et2: Eternitas) => onImportKeyPair(file, walletNative, et2))
  }

  def onImportKeyPair(file: File, walletNative: WalletNative, et2: Eternitas)(
      implicit $ : JQueryWrapper,
      feedback: UserFeedback,
      executionContext: ExecutionContext): Unit = SymCrypto.importKey(
    et2,
    walletNative,
    (et3) => LastWillStartup.init(AsymCrypto.importPinata(et3, walletNative))
  )

  def pinData(dataHash: String,
              ivHash: String,
              eternitas: Eternitas,
              cb: (Eternitas) => js.Any)(implicit $ : JQueryWrapper,
                                         userFeedback: UserFeedback) = {
    cb(eternitas)
  }

  private def handlePinResult(eternitas: Eternitas,
                              axiosResponse: AxiosResponse,
                              axiosResponse2: AxiosResponse)(
      implicit $ : JQueryWrapper,
      userFeedback: UserFeedback) = {

    val dataHash = axiosResponse.asInstanceOf[PinataPinResponse].data.IpfsHash
    val ivHash = axiosResponse2.asInstanceOf[PinataPinResponse].data.IpfsHash
    pinData(dataHash, ivHash, eternitas, e => {
      LastWillStartup.init(eternitas)
      userFeedback.message(s"Your data is encrypted and stored!")
    })

  }
}
