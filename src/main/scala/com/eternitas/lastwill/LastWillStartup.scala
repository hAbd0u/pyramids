package com.lyrx.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQueryEventObject
import org.scalajs.dom.document
import org.scalajs.dom.raw._
import com.eternitas.lastwill.DropDragHandler._
import com.eternitas.lastwill.HashSum._

import scala.scalajs.js ;

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

  }

   def init()(implicit $:JQueryWrapper)={



     $("#drop_zone").
       onDrop(
         (file:File)=> new FileReader().
           onHash(file,aHash=>
            $("#drop_zone").html(aHash))
       ).onDragOverNothing()
   }


  private def initJQuery():JQueryWrapper = {
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]
  }


}
