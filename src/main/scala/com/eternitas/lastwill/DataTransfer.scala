package com.eternitas.lastwill

import org.querki.jquery.JQueryEventObject
import org.scalajs.dom.raw.{Blob, EventTarget}

import scala.scalajs.js

@js.native
trait DataTransferTarget extends EventTarget {


  val files:js.UndefOr[js.Array[Blob]] = js.native

}


@js.native
trait DataTransferEvent extends JQueryEventObject {

   val dataTransfer:DataTransferTarget = js.native

   val originalEvent:DataTransferEvent=js.native

}
