package com.lyrx.eternitas.lastwill

import com.eternitas.wizard.{Bookmark, ContentSeqImpl, JQueryWrapper, WizardImpl}
import org.querki.jquery.{JQueryEventObject, JQueryXHR}
import org.scalajs.dom.{XMLHttpRequest, document}
import org.scalajs.dom.raw.{Element, Event}

import scala.scalajs.js

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
     $("#drop_zone").on("drop",(e:Element, evt:JQueryEventObject, t2:Any, t3:Any)=>{
       evt.stopPropagation();
       evt.preventDefault();
       println("Dropped!!")

     })

     $("#drop_zone").on("dragover",(e:Element, evt:JQueryEventObject, t2:Any, t3:Any)=>{
       evt.stopPropagation();
       evt.preventDefault();
       println("Dragged!!")

     })


   }

  private def initJQuery():JQueryWrapper = {
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]
  }


}
