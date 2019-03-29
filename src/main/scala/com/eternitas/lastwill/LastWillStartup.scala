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
                              (e: Event) => new Eternitas(
                                  keyPairOpt = None,
                                  pinnataOpt = None,
                                keyOpt = None,
                                pinDataOpt = None
                                ).withAllKeys().onComplete(t=>{
                                implicit val $ = initJQuery()
                                def showTime(): Unit = $("#time").html(new js.Date().toLocaleString())

                                showTime()
                                implicit val feedBack:UserFeedback = new UserFeedback {
                                  override def message(s: String): Unit = {
                                    msgField().removeClass("error-message").
                                      addClass("normal-message").
                                      html(s)
                                    showTime();
                                  }

                                  override def error(s: String): Unit = {
                                    msgField().
                                      removeClass("normal-message").
                                      addClass("error-message")
                                      .html(s);
                                    showTime();
                                  }
                                }
                                t.map(eternitas=>init(eternitas))
                                t.failed.map(thr=>feedBack.error(thr.getMessage))
                              }
                                )
                              )

  def init(et: Eternitas)(implicit $ : JQueryWrapper,userFeedback: UserFeedback): Unit = {
    $("#logo").off().export(et).iimport(et)
    $("#drop_zone").off().upLoad(et)
    $("#data-display").dataDisplay(et)
  }


  def initJQuery(): JQueryWrapper =
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]

}
