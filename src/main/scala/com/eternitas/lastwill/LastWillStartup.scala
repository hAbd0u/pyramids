package com.lyrx.eternitas.lastwill

import com.eternitas.lastwill.Import.ImportJQuery
import com.eternitas.lastwill.Pinning.PPinning
import com.eternitas.lastwill.Upload.PUpload
import com.eternitas.lastwill.Loading.PLoading
import com.eternitas.lastwill.axioss.Pinata
import com.eternitas.lastwill.cryptoo.{HashSum, SymCrypto}
import com.eternitas.lastwill.{Buffers, Eternitas, PimpedJQuery, UserFeedback}
import com.eternitas.wizard.JQueryWrapper
import org.scalajs.dom.document
import org.scalajs.dom.raw._

import scala.concurrent.ExecutionContext
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.util.Try

object LastWillStartup {
  implicit val ec = ExecutionContext.global;
  def msgField()(implicit $ : JQueryWrapper) = $("#message")

  def createFeedBack()(implicit $ : JQueryWrapper): UserFeedback =
    new UserFeedback {
      override def showTime(): UserFeedback = {
        $("#time").html(new js.Date().toLocaleString())
        this;
      }
      override def message(s: String): Unit = {
        msgField()
          .removeClass("error-message")
          .addClass("normal-message")
          .html(s)
        showTime();
      }

      override def error(s: String): Unit = {
        msgField()
          .removeClass("normal-message")
          .addClass("error-message")
          .html(s);
        showTime();
      }

      override def log(msg: String, b: ArrayBuffer)(
          implicit exc: ExecutionContext): Unit =
        HashSum
          .hash(b)
          .onComplete(t =>
            t.map(b => {
              import Buffers._
              logString(s"${msg}: ${b.toHexString()}")
            }))

      override def logString(msg: String): Unit = println(msg)
    }

  def main(args: Array[String]): Unit =
    document.addEventListener(
      "DOMContentLoaded",
      (e: Event) =>
        new Eternitas(
          keyPairOpt = None,
          pinataAuth = None,
          keyOpt = None,
          pinDataOpt = initPinDataOpt()
        ).withAllKeys()
          .onComplete(t => initEternitas(t))
    )

  def initPinDataOpt(): Option[String] = PimpedJQuery.currentHash()


  def initEternitas(t: Try[Eternitas]): Try[Unit] = {

    implicit val $ = initJQuery()
    implicit val feedBack: UserFeedback = createFeedBack().showTime()
    t.map(eternitas => init(eternitas))
    t.failed.map(thr => feedBack.error(thr.getMessage))

  }

  def init(et: Eternitas)(implicit $ : JQueryWrapper,
                          feedback: UserFeedback): Unit = {
    //SymCrypto.test(et.keyOpt.get,"123456789")
    $("#logo").off().export(et).iimport(et)
    $("#drop_zone").off().upLoad(et)
    $("#data-display").empty().dataDisplay(et)
    $("#cid").empty().off().cidEntered(et)
    $("#pinfolder").empty()
    $("#pinata").empty()


    et.pinataAuth.map(p => {
      new Pinata(p).authenticate(
        p2 => $("#pinata").html(s"Pinnata: ${p.api}"),
        e => feedback.error(s"Pinnata error ${e}")
      )
    })
    et.pinDataOpt.map(pd =>{
      $("#pinfolder").html(s"DATA:  <a href='${PimpedJQuery.resolveUrl(pd)}' "+
                                     s"class='pinned' "+
                                     s" id='${pd}' "+
                                     s" download='pindata.json' target='_blank'>"+
                                     s"${pd}"+
                                     s"</a>" )


    })

  }

  def initJQuery(): JQueryWrapper =
    js.Dynamic.global.jQuery
      .noConflict()
      .asInstanceOf[JQueryWrapper]

}
