package com.eternitas.lastwill

import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom
import dom.raw._

import scala.concurrent.ExecutionContext
import scala.util.Try
import com.eternitas.lastwill.HashSum._
import org.scalajs.dom.Window

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal


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
              map((blob)=> h(blob.asInstanceOf[File])))))

      })


    def onDragOverNothing():JQuery={
      jq.on("dragover",(e:Element, evt:JQueryEventObject, t2:Any, t3:Any)=>{
        evt.stopPropagation();
        evt.preventDefault();
      })
    }


    def handleExport(eternitas: Eternitas)(implicit ctx:ExecutionContext)=
      jq.click((e: Event) => eternitas.
        withKeys().
        onComplete((f2:Try[Eternitas])=>f2.map((eternitas:Eternitas)=>
          eternitas.export().
            onComplete(t=>if(t.isFailure) println("Export failed for keypar!" )
            else t.map((s:String)=>{


              println("Url: "+
                mywindow.URL.createObjectURL(new Blob(js.Array(s),BlobPropertyBag("octet/stream"))))



              //dom.window.location.assign()
            //  dom.document.URL.


              //println("Have export: " + s)
            })))))



    def handleDrop()=onDrop(
        (file: File) =>
          new FileReader().onHash(file, aHash => {
            jq.removeClass("drop").
              addClass("dropped").
              html(aHash)
          })
      ).onDragOverNothing()




  }






}
