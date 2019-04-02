package com.eternitas.lastwill

import com.eternitas.lastwill.axioss._
import com.eternitas.lastwill.cryptoo._
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.JQuery
import org.scalajs.dom.raw.{File, FileReader}
import com.eternitas.lastwill.Buffers._

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.util.Try
import js.Dynamic.{literal => l}



object Upload {

  implicit class PUpload(jq: JQuery) extends PimpedJQuery.PJQuery(jq){



    def upLoad(eternitas: Eternitas)(implicit ctx: ExecutionContext,
                                     $: JQueryWrapper,
                                     feedback: UserFeedback) = onDrop(
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


    def onEncryptionResult(t: Try[SymEncryptionResult],
                           eternitas: Eternitas,
                           file: File)(
                            implicit feedback: UserFeedback,
                            $: JQueryWrapper,
                            executionContext: ExecutionContext) = {

      t.failed.map(thr =>
        feedback.error(s"Encryption failed: ${thr.getMessage()}"))
      t.map((symEncryptionResult: SymEncryptionResult) => {
        feedback.log(s"Encrypted", symEncryptionResult.result)
        feedback.log(s"IV", symEncryptionResult.iv)
        eternitas.pinataAuth.map(p =>
          withPinata(eternitas, file, symEncryptionResult, p))
      })

    }

    def withPinata(eternitas: Eternitas,
                   file: File,
                   symEncryptionResult: SymEncryptionResult,
                   p: PinataAuth)(implicit feedback: UserFeedback,
                                  executionContext: ExecutionContext,
                                  $: JQueryWrapper): AxiosImpl = {

      feedback.message("Start pinning, please be very patient!")
      new Pinata(p)
        .pinFileToIPFS(symEncryptionResult.result, PinataMetaData(file))
        .`then`((axiosResponse) =>
          pinataUpload(file, eternitas, symEncryptionResult, p, axiosResponse))
        .`catch`((error) => feedback.message(s"Error pinning hash: ${error}"))

    }

    def pinataUpload(file: File,
                     eternitas: Eternitas,
                     symEncryptionResult: SymEncryptionResult,
                     p: PinataAuth,
                     axiosResponse: AxiosResponse)(
                      implicit $: JQueryWrapper,
                      feedback: UserFeedback): AxiosImpl = {

      new Pinata(p)
        .pinFileToIPFS(symEncryptionResult.iv,
          PinataMetaData(
            axiosResponse
              .asInstanceOf[PinataPinResponse]
              .data))
        .`then`((axiosResponse2) =>
          handlePinResult(file, eternitas, axiosResponse, axiosResponse2))
        .`catch`((error) => feedback.message(s"Pinning error: ${error}"))

    }

    private def handlePinResult(file: File,
                                eternitas: Eternitas,
                                axiosResponse: AxiosResponse,
                                axiosResponse2: AxiosResponse)(
                                 implicit $: JQueryWrapper,
                                 userFeedback: UserFeedback) = {

      val dataHash = axiosResponse.asInstanceOf[PinataPinResponse].data.IpfsHash
      val ivHash = axiosResponse2.asInstanceOf[PinataPinResponse].data.IpfsHash

      userFeedback.logString(s"Data uploaded: ${dataHash}")
      userFeedback.logString(s"IV uploaded: ${ivHash}")

      pinDataList(file, dataHash, ivHash, eternitas, e => {
        LastWillStartup.init(e)
        userFeedback.message(s"Your data is encrypted and stored!")
      })

    }

    def pinDataList(file: File,
                    dataHash: String,
                    ivHash: String,
                    eternitas: Eternitas,
                    cb: (Eternitas) => js.Any)(implicit $: JQueryWrapper,
                                               userFeedback: UserFeedback) = {
      import WalletNative._;

      def havePinData()={
        eternitas.pinDataOpt.isDefined &&
        eternitas.pinDataOpt.map(s=> (s != "")).getOrElse(false)
      }

      def mLoadPinData(cb: (PinDataListNative) => Unit) =
        if (havePinData())
          eternitas.pinDataOpt.map((aHash: String) =>
            PimpedJQuery.loadHashAsText(aHash, (s: String) => {
              cb(js.JSON.parse(s).asInstanceOf[PinDataListNative])
            }))
        else
          cb(l("data" -> js.Array()).asInstanceOf[PinDataListNative])

      mLoadPinData((pinDataNative) => {
        val pinString = Eternitas.stringify(
          pinDataNative.withPinData(
            l("hash" -> dataHash,
              "vc" -> ivHash,
              "name" -> file.name,
              "type" -> file.`type`).asInstanceOf[PinDataNative]))

        eternitas.pinataAuth.map(auth => {
          // userFeedback.logString("Pinning: " + pinString)
          new Pinata(auth)
            .pinFileToIPFS(
              pinString.toArrayBuffer(),
              PinataMetaData(name = Some("Eternitas-Data"),
                size = None,
                `type` = Some("application/json"))
            )
            .`then`((r: AxiosResponse) => {
              val eternitasHash = r.asInstanceOf[PinataPinResponse].data.IpfsHash
              userFeedback.logString(s"Pinned Eternitas-Data: ${eternitasHash}")
              cb(eternitas.withPinDataHash(eternitasHash))
            })
            .`catch`((e: AxiosError) => {
              userFeedback.error(s"Error pinning eternitas data list: ${e}")
              cb(eternitas)
            })
        })
      })
    }



  }













}
