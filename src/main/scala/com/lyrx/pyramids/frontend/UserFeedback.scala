package com.lyrx.pyramids.frontend
import typings.jqueryLib.JQuery
import typings.jqueryLib.^.{jQuery => $}


import scala.scalajs.js

trait UserFeedback {


   def msgField[T]():JQuery[T]
   def timeField[T]():JQuery[T]


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
