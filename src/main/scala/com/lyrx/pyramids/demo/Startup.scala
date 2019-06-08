package com.lyrx.pyramids.demo

import org.scalajs.dom.{Event, File, document}
import typings.jqueryLib.{
  JQuery,
  JQueryEventObject,
  JQueryStatic,
  jqueryMod => jq
}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.{UndefOr, |}

object Startup  {
  implicit val ec = ExecutionContext.global

  type JQueryOb = org.scalablytyped.runtime.TopLevel[JQueryStatic]
  type TextFieldContents =
    js.UndefOr[java.lang.String | scala.Double | js.Array[java.lang.String]]



  def main(args: Array[String]): Unit =
    document.addEventListener("DOMContentLoaded", (e: Event) => startup())

  def startup() = {

    jq("#message")html("Hello India!")


  }


}
