package com.eternitas.lastwill

import com.eternitas.lastwill.Buffers._
import com.eternitas.lastwill.PinData.PinningData
import com.eternitas.lastwill.axioss.{AxiosResponse, Pinata, PinataMetaData, PinataPinResponse}
import com.eternitas.lastwill.cryptoo.{AsymCrypto, SymCrypto, SymEncryptionResult}
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
  val URL:URL = js.native

}


@js.native
trait URL extends js.Any{

  def createObjectURL(blob:Blob):String = js.native


}

object Actions {
  val mywindow = js.Dynamic.global.window.asInstanceOf[MyWindow]

  implicit class PimpedJQuery(jq:JQuery){

    def onDrop(h:(File=>Unit)):JQuery=jq.
      on("drop",(evt: JQueryEventObject) => {
        evt.
          stopPropagation();
        evt.preventDefault();
        evt.
          asInstanceOf[DataTransferEvent].
          originalEvent.
          map(_.dataTransfer.map(_.files)).
          map(_.map(_.map(
            _.headOption.
              map((blob)=> h(blob.asInstanceOf[File])))))})


    def onDragOverNothing():JQuery={
      jq.on("dragover",(e:Element, evt:JQueryEventObject, t2:Any, t3:Any)=>{
        evt.stopPropagation();
        evt.preventDefault();
      })
    }


    def export(eternitas: Eternitas)(implicit ctx:ExecutionContext,$:JQueryWrapper,
                                     feedback: UserFeedback)=
      jq.click((e: Event) =>  eternitas.export().
            onComplete(t=>if(t.isFailure) feedback.error("Export failed for keypair: " + t )
            else t.map((s:String)=>{
              val blob:Blob =
                new Blob(js.Array(s),BlobPropertyBag("octet/stream"))
              val url:String = mywindow.URL.createObjectURL(blob)
              dom.window.location.assign(url)})))



    def upLoad(eternitas: Eternitas)(implicit ctx:ExecutionContext,$:JQueryWrapper,
                                     feedback: UserFeedback)=onDrop(
        (file: File) => eternitas.keyOpt.map(key => new FileReader().
          onReadArrayBuffer(file,
            (arrayBuffer:ArrayBuffer) => SymCrypto.
            encrypt(key,arrayBuffer).onComplete(
            (t:Try[SymEncryptionResult])=>{
              t.failed.map(thr=> feedback.error(s"Encryption failed: ${thr.getMessage()}"))
              t.map((symEncryptionResult:SymEncryptionResult)=> {
                feedback.message(s"Encrypted: ${file.name}")
                eternitas.pinnataOpt.map(p=> {
                  feedback.message("Start pinning, please be very patient!")
                new Pinata(p).
                  pinFileToIPFS(symEncryptionResult.result,PinataMetaData(file)).
                  `then`((axiosResponse) => {
                    new Pinata(p).pinFileToIPFS(
                      symEncryptionResult.iv.buffer,
                      PinataMetaData(axiosResponse.asInstanceOf[PinataPinResponse].data)).
                    `then`((axiosResponse2)=> handlePinResult(eternitas, axiosResponse,axiosResponse2)).
                      `catch`((error) => feedback.message(s"Error pinning iv: ${error}"))
                  }
                  ).
                `catch`((error) => feedback.message(s"Error pinning hash: ${error}"))
              })})
            })))
    ).onDragOverNothing()



    def loadHashAsArrayBuffer(aHash:String,cb:(ArrayBuffer)=>Unit)(implicit
      $:JQueryWrapper) = {
      val r = new XMLHttpRequest()
      r.open("GET", s"/ipfs/${aHash}", true);
      r.responseType="arraybuffer"
      r.onload = (oEvent:Event) => cb(r.response.asInstanceOf[ArrayBuffer]);
      r.send()
    }
    def loadHashAsText(aHash:String,cb:(String)=>Unit)(implicit
                                                                                       $:JQueryWrapper) = {
      val r = new XMLHttpRequest()
      r.open("GET", s"/ipfs/${aHash}", true);
      r.responseType="text"
      r.onload = (oEvent:Event) => cb(r.response.toString());
      r.send()
    }




    def showPinned(pinned: PinData.Pinned,eternitas: Eternitas)(
      implicit ctx:ExecutionContext,
      $:JQueryWrapper,
      feedback: UserFeedback) = {
      val el = $(s"<a href='#' class='pinned'>${pinned.name.getOrElse("[UNNAMED]")}</a>")
      $("#data-display").append(el)
      el.click((event:Event)=>
        pinned.`hash`.map(aHash=> loadHashAsArrayBuffer(aHash,(encryptedData:ArrayBuffer)=>
          pinned.vc.map(avchash=>loadHashAsArrayBuffer(avchash,(vc:ArrayBuffer)=> eternitas.keyOpt.map(symKey =>
                    {
                    val f = SymCrypto.decrypt(symKey,
                      encryptedData,
                      new Uint8Array(vc))
                    f.map( (t:ArrayBuffer)=> feedback.message("Decryption successfull"))
                    f.failed.map (e=>feedback.error(s"Decryption failed: ${e.getLocalizedMessage}"))
                  })
            )))))
    }

    def dataDisplay(eternitas: Eternitas)(
      implicit ctx:ExecutionContext,
      $:JQueryWrapper,
      feedback: UserFeedback) = eternitas.pinDataOpt.
      map(aHash=> loadHashAsText(aHash,(data:String)=>
        js.JSON.parse(data.toString).asInstanceOf[PinningData].data.
            foreach(pinned=>showPinned(pinned,eternitas)))
      )

    def iimport(oldEternitas: Eternitas)(
      implicit ctx:ExecutionContext,
      $:JQueryWrapper,
      feedback: UserFeedback)=onDrop(
      (file: File) =>
        new FileReader().onReadArrayBuffer(file, bufferSource => {
          if(file.`type` == "application/json"){
            val importData:js.Dynamic = js.JSON.parse(bufferSource.toNormalString())
            val withPinFolder  = if(importData.pinfolder.hash.toString()=="")
              oldEternitas
            else
              oldEternitas.withPinData(importData.pinfolder.hash.toString)

            withPinFolder.pinDataOpt.map(pd=>$("#pinfolder").html(s"DATA: ${pd}"))

            AsymCrypto.importKeyPair(withPinFolder,
              importData ,
              (et:Eternitas)=>{
                PinataMetaData(Some(file.name), Some(file.size), Some(file.`type`))
                val et2 =AsymCrypto.importPinata(et,importData)
                et2.pinnataOpt.map(p=>new Pinata(p).authenticate(
                  p2=>$("#pinata").html(s"Pinnata: ${p.api}"),
                  e=>feedback.error(s"Pinnata error ${e}")
                ))
                et2.keyPairOpt.map(keys=>{
                  LastWillStartup.init(et2)
                  feedback.message("Loaded asym key pair")
                })
                if(et2.keyPairOpt.isEmpty) feedback.error("No asym key pair")
              })
          }
          else{
            feedback.error("Unsupported data type: " + file.`type`)
          }
        })
    ).onDragOverNothing()



  }


  def pinData(dataHash: String,
              ivHash: String,
              eternitas:Eternitas,
              cb: (Eternitas)=>js.Any)(implicit $: JQueryWrapper,userFeedback: UserFeedback) = {


    /*
    eternitas.pinDataOpt.map(pd=>{
      $.get(s"/ipfs/${pd}","",(data,status,xhr)=>{})
    }).getOrElse("")
    */

    cb(eternitas)
  }

  private def handlePinResult(eternitas: Eternitas,
                              axiosResponse: AxiosResponse,
                              axiosResponse2: AxiosResponse)(implicit $: JQueryWrapper,userFeedback: UserFeedback) = {

    val dataHash = axiosResponse.asInstanceOf[PinataPinResponse].data.IpfsHash
    val ivHash = axiosResponse2.asInstanceOf[PinataPinResponse].data.IpfsHash
    pinData(dataHash,ivHash,eternitas,e=>{
      LastWillStartup.init(eternitas)
      userFeedback.message(s"Your data is encrypted and stored!")
    })

  }
}
