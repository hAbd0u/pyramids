package com.eternitas.lastwill

import com.eternitas.lastwill.Buffers._
import com.eternitas.lastwill.HashSum._
import com.eternitas.wizard.JQueryWrapper
import com.lyrx.eternitas.lastwill.LastWillStartup
import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom
import org.scalajs.dom.raw._

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.ArrayBuffer
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



    def upLoad(eternitas: Eternitas)(implicit ctx:ExecutionContext,
                                     feedback: UserFeedback)=onDrop(
        (file: File) =>
          new FileReader().onRead(file, (arrayBuffer:ArrayBuffer) => {
            jq.removeClass("drop").
              addClass("dropped").
              html("Encrypting, please wait ..")
              Encrypt.
                encrypt(eternitas.keysOpt.get,arrayBuffer).onComplete(
                (t:Try[ArrayBuffer])=>{
                  t.failed.map(thr=>
                    feedback.error("Encryption failed: " +thr)
                  )
                  t.map(r=>feedback.message("File encrypted: " + file.name))
                })})).onDragOverNothing()



    def iimport(oldEternitas: Eternitas)(
      implicit ctx:ExecutionContext,
      $:JQueryWrapper,
      feedback: UserFeedback)=onDrop(
      (file: File) =>
        new FileReader().onRead(file, bufferSource => {
          if(file.`type` == "application/json"){
            val importData:js.Dynamic = js.JSON.parse(bufferSource.toNormalString())
            Encrypt.importKeyPair(oldEternitas,
              importData ,
              (et:Eternitas)=>{
                val et2 =Encrypt.importPinata(et,importData)
                et2.pinnataOpt.map(p=>new Pinata(p).authenticate(
                  p2=>feedback.message(s"Authenticated to pinnata: ${p.api}"),
                  e=>feedback.error(s"Pinnata error ${e}")
                ))
                LastWillStartup.init(et2)
                //et2.pinnataOpt.map(p=>feedback.message("Pinnata: " + p.api))
              })
          }
          else{
            feedback.error("Unsupported data type: " + file.`type`)
          }
        })
    ).onDragOverNothing()



  }






}
