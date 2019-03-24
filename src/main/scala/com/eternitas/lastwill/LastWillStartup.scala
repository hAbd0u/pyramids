package com.lyrx.eternitas.lastwill


import com.eternitas.lastwill.Actions._
import com.eternitas.lastwill.Eternitas
import com.eternitas.wizard.JQueryWrapper
import org.scalajs.dom.document
import org.scalajs.dom.raw._

import scala.concurrent.ExecutionContext
import scala.scalajs.js



object LastWillStartup {
  implicit val ec = ExecutionContext.global;

  def main(args: Array[String]): Unit = document.addEventListener(
      "DOMContentLoaded",
      (e: Event) => {
        implicit val $ = initJQuery()
        $("#logo").
          export(new Eternitas()).
          iimport()
        $("#drop_zone").upLoad()
      }
    )

  private def initJQuery(): JQueryWrapper = js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]


}
