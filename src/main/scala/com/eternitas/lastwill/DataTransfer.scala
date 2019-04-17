package com.eternitas.lastwill


import org.scalajs.dom.raw.{Blob, EventTarget}
import org.scalajs.jquery.JQueryEventObject

import scala.scalajs.js
import scala.scalajs.js.UndefOr

@js.native
trait DataTransferTarget extends EventTarget {


  val files:js.UndefOr[js.Array[Blob]] = js.native

}


@js.native
trait DataTransferEvent extends JQueryEventObject {

   val dataTransfer:UndefOr[DataTransferTarget] = js.native

   val originalEvent:UndefOr[DataTransferEvent]=js.native

}
