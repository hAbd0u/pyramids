package com.lyrx.eternitas.lastwill

import com.eternitas.lastwill.Actions._
import com.eternitas.lastwill.{Eternitas, UserFeedback}
import com.eternitas.wizard.JQueryWrapper
import org.scalajs.dom.document
import org.scalajs.dom.raw._

import scala.concurrent.ExecutionContext
import scala.scalajs.js

object LastWillStartup {
  implicit val ec = ExecutionContext.global;
  def msgField()(implicit $:JQueryWrapper)=$("#message")

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded",
                              (e: Event) => init(
                                new Eternitas(
                                  keysOpt = None,
                                  pinnataOpt = None
                                ))(initJQuery()))

  def init(et: Eternitas)(implicit $ : JQueryWrapper): Unit = {
    implicit val feedBack:UserFeedback = new UserFeedback {
      override def message(s: String): Unit = {
        msgField().removeClass("error-message").
          html(s)
      }

      override def error(s: String): Unit = {
        msgField().
          addClass("error-message")
          .html(s);
      }
    }

    $("#logo").off().export(et).iimport(et)
    $("#drop_zone").off().upLoad(et)
    initAxios()
  }

  def initAxios()(implicit $ : JQueryWrapper,feedback: UserFeedback): Unit ={

    //feedback.message("Axios: " + axios)

  }
  def initJQuery(): JQueryWrapper =
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]

}
