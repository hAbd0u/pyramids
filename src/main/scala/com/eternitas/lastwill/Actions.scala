package com.eternitas.lastwill

import org.querki.jquery.{JQuery, JQueryEventObject}
import org.scalajs.dom.raw.{Element, Event, File, FileReader}

import scala.concurrent.ExecutionContext
import scala.util.Try
import com.eternitas.lastwill.HashSum._


object Actions {

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
            onComplete(t=>if(t.isFailure)
              println("Export failed:" + t.toString)
            else t.map((s)=>println("Have export: " + s))))))



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
