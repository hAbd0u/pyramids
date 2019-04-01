package com.lyrx.eternitas.lastwill

import com.eternitas.lastwill.Actions._
import com.eternitas.lastwill.Import.ImportJQuery
import com.eternitas.lastwill.Upload.PUpload
import com.eternitas.lastwill.axioss.Pinata
import com.eternitas.lastwill.cryptoo.{HashSum, SymCrypto}
import com.eternitas.lastwill.{Buffers, Eternitas, UserFeedback}
import com.eternitas.wizard.JQueryWrapper
import org.scalajs.dom.document
import org.scalajs.dom.raw._

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

object LastWillStartup {
  implicit val ec = ExecutionContext.global;
  def msgField()(implicit $:JQueryWrapper)=$("#message")

  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded",
                              (e: Event) => new Eternitas(
                                  keyPairOpt = None,
                                  pinataAuth = None,
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

                                  override def log(msg:String, b: ArrayBuffer)(implicit exc:ExecutionContext): Unit =
                                    HashSum.hash(b).onComplete(t=>t.map(b=>{
                                      import Buffers._
                                      logString( s"${msg}: ${b.toHexString()}")
                                    }))

                                  override def logString(msg: String): Unit = println(msg)
                                }
                                t.map(eternitas=>init(eternitas))
                                t.failed.map(thr=>feedBack.error(thr.getMessage))
                              }
                                )
                              )

  def init(et: Eternitas)(implicit $ : JQueryWrapper,feedback: UserFeedback): Unit = {
    //SymCrypto.test(et.keyOpt.get,"123456789")
    $("#logo").off().export(et).iimport(et)
    $("#drop_zone").off().upLoad(et)
    $("#data-display").empty().dataDisplay(et)





    et.pinataAuth.map(
      p => {
        new Pinata(p).authenticate(
          p2 => $("#pinata").html(s"Pinnata: ${p.api}"),
          e => feedback.error(s"Pinnata error ${e}")
        )
      })
    et.pinDataOpt.map(pd =>
      $("#pinfolder").html(s"DATA: ${pd.substring(0,10)}[...]"))

  }


  def initJQuery(): JQueryWrapper =
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]

}
