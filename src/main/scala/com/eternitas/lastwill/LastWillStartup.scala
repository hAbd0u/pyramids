package com.lyrx.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQueryEventObject
import org.scalajs.dom.document
import org.scalajs.dom.raw._
import com.eternitas.lastwill.DropDragHandler._
import com.eternitas.lastwill.MD5Sum._

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
         (file:File)=> {
           new FileReader().onHash(file,aHash=>println("Hash: " + aHash))
           //println("Dropped:" + file.`name`)
           }
       ).onDragOverNothing()

     println("Eternitas is initialized: " +js.Dynamic.global.SparkMD5.ArrayBuffer)




   }


  private def initJQuery():JQueryWrapper = {
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]
  }


}
