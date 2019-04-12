package com.eternitas.lastwill

import com.eternitas.lastwill.Buffers._
import com.eternitas.lastwill.axioss.{AxiosImpl, Pinata, PinataMetaData, PinataPinResponse}
import com.eternitas.lastwill.cryptoo.{AsymCrypto, SymCrypto, WalletNative}
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.JQuery
import org.scalajs.dom.Event
import org.scalajs.dom.crypto.CryptoKey
import org.scalajs.dom.raw.{File, FileReader}

import scala.concurrent.ExecutionContext
import scala.scalajs.js
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

          feedback.message("Signing, please be very patient ...")


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
                eternitas,
                eternitas.
                  config.
                  signKeyPair.
                  keyPairOpt.
                  get.
                  publicKey

              )).
              `catch`(e=>feedback.error(e.toString))
          )))))
        }
      )

      onDrop( (f:File)=>new FileReader().onReadArrayBuffer(f,b=>importSignKey(
          eternitas, f, b))).onDragOverNothing()
    }

    def importSignKey(oldEternitas: Eternitas,
                       file: File,
                       bufferSource: ArrayBuffer)(
                        implicit $: JQueryWrapper,
                        feedback: UserFeedback,
                        executionContext: ExecutionContext) =
            AsymCrypto.importSignKeyPair(
              file,
              oldEternitas,
              getNativeData(bufferSource),
              newEternitas=>
            LastWillStartup.init(newEternitas)
          )






    def onPinnedSignature(
                           r: PinataPinResponse,
                           pinData:String,
                           auth:AllCredentials,
                           nameOpt:Option[String],
                           eternitas: Eternitas,
                           pubKey:CryptoKey
                         )(implicit ctx: ExecutionContext,
                           $ : JQueryWrapper,
                           feedback: UserFeedback): Unit = AsymCrypto.
        eexportKey(pubKey).
        onComplete(t=>
          t.map(webKey=>new Pinata(auth).pinFileToIPFS(
            Eternitas.stringify(l(
              "verify" -> webKey,
              "signature" -> r.data.IpfsHash,
              "data" -> pinData,
              "description" -> eternitas.
                config.
                signKeyPair.
                nameOpt.map(s=>s"SIGNATURE-MAPPING ${s}").
                getOrElse("SIGNATURE-MAPPING").toString(),
              "date" -> new js.Date()
            )).toArrayBuffer(),
            nameOpt.map(name=>PinataMetaData(Some(s"SIGNATURE-MAPPING ${name}"),None,None)).
              getOrElse(PinataMetaData(Some(s"SIGNATURE-MAPPING"),None,None))).
            `then`(r=>{
              val hash = r.asInstanceOf[PinataPinResponse].data.IpfsHash
              feedback.message(s"SIGNATURE: ${hash}")
              LastWillStartup.init(eternitas.withSignature(hash))
            }).
            `catch`(e=>feedback.error(e.toString()))))


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
