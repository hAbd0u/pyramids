package com.lyrx.eternitas.lastwill

import java.security.CryptoPrimitive

import com.eternitas.lastwill.DataTransferEvent
import com.eternitas.wizard.{Bookmark, ContentSeqImpl, JQueryWrapper, WizardImpl}
import org.querki.jquery.{JQueryEventObject, JQueryXHR}
import org.scalajs.dom.{XMLHttpRequest, document}
import org.scalajs.dom.raw.{Blob, Element, Event, FileReader}

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object LastWillStartup {



  def main(args: Array[String]): Unit = {
    document.addEventListener(
      "DOMContentLoaded",
      (e: Event) => onStartup()
    )
  }



  private def onStartup() = {
    implicit val $ = initJQuery()

    init()
    println("Eternitas is initialized ")

  }

   def init()(implicit $:JQueryWrapper)={
     $("#drop_zone").on("drop",handler = (evt: JQueryEventObject) => {
       evt.stopPropagation();
       evt.preventDefault();

       evt.
         asInstanceOf[DataTransferEvent].
         originalEvent.
         map(_.dataTransfer.map(_.files)).
         map(_.map(_.map(
           _.headOption.
             map((blob:Blob)=> println("Dropped:" + blob.`type`)))))




     })

     $("#drop_zone").on("dragover",(e:Element, evt:JQueryEventObject, t2:Any, t3:Any)=>{
       evt.stopPropagation();
       evt.preventDefault();

      // println("Dragged!!")

     })


   }

  private def initJQuery():JQueryWrapper = {
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]
  }


}
