package com.lyrx.pyramids

import scala.concurrent.ExecutionContext
import scala.scalajs.js.typedarray.ArrayBuffer

trait UserFeedback {
  def showTime(): UserFeedback
  def message(s:String)
  def error(s:String)
  def log(msg:String,b:ArrayBuffer)(implicit exc:ExecutionContext)
  def logString(msg:String)

}
