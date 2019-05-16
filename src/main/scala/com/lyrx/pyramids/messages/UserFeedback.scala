package com.lyrx.pyramids.messages

import org.scalajs.jquery.JQuery

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

trait UserFeedback {



  def msgField():JQuery
  def timeField():JQuery

   def showTime(): UserFeedback = {
    timeField().html(new js.Date().toLocaleString())
    this;
  }
   def message(s: String): Unit = {
     msgField()
      .removeClass("error-message")
      .addClass("normal-message")
      .html(s)
    showTime();
  }

   def error(s: String): Unit = {
    msgField()
      .removeClass("normal-message")
      .addClass("error-message")
      .html(s);
    showTime();
  }



   def logString(msg: String): Unit = println(msg)



}
