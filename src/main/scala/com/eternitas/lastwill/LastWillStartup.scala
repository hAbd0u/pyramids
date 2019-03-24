package com.lyrx.eternitas.lastwill

import com.eternitas.wizard.JQueryWrapper
import org.querki.jquery.JQueryEventObject
import org.scalajs.dom.document
import org.scalajs.dom.raw._
import com.eternitas.lastwill.DropDragHandler._
import com.eternitas.lastwill.Eternitas
import com.eternitas.lastwill.HashSum._
import org.scalajs.dom
import org.scalajs.dom.crypto.JsonWebKey

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.typedarray.ArrayBuffer
import scala.scalajs.js.JSON
import scala.util.Try;



object LastWillStartup {
  implicit val ec = ExecutionContext.global;

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

  def logo()(implicit $ : JQueryWrapper) =
    $("#logo").click((e: Event) =>
      new Eternitas().
        withKeys().
        onComplete((f2:Try[Eternitas])=>f2.map((eternitas:Eternitas)=>
          eternitas.export().
            onComplete(t=>if(t.isFailure)
                  println("Export failed:" + t.toString)
              else t.map((s)=>println("Have export: " + s))))))



  def init()(implicit $ : JQueryWrapper) = {
    logo()
    dropZone()
  }

  def dropZone()(implicit $ : JQueryWrapper) = $("#drop_zone").
    onDrop(
        (file: File) =>
          new FileReader().onHash(file, aHash => {
            $("#drop_zone").removeClass("drop").
              addClass("dropped").
              html(aHash)
          })
      ).onDragOverNothing()


  private def initJQuery(): JQueryWrapper = {
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]
  }

}
