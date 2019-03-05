package com.lyrx.eternitas.lastwill

import com.eternitas.wizard.{Bookmark, ContentSeqImpl, JQueryWrapper, WizardImpl}
import org.querki.jquery.JQueryXHR
import org.scalajs.dom.{XMLHttpRequest, document}
import org.scalajs.dom.raw.Event

import scala.scalajs.js

object LastWillStartup {

  val bookName = "koblach"

  def main(args: Array[String]): Unit = {
    document.addEventListener(
      "DOMContentLoaded",
      (e: Event) => {
      }
    )
  }

}
