package com.eternitas.lastwill.axioss

import org.scalajs.dom.raw.FormData

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.ArrayBuffer





@js.native
@JSGlobal
object axios extends AxiosImpl {

}

@js.native
trait AxiosImpl extends js.Object {

  def get(address:String, data:js.Dynamic):AxiosImpl = js.native

  def `then`(callback: js.Function1[AxiosResponse,js.Any]):AxiosImpl = js.native

  def `catch`(callback: js.Function1[AxiosError,js.Any]):AxiosImpl = js.native

  def post(address:String, data:FormData, headers:js.Dynamic):AxiosImpl = js.native


}


@js.native
trait AxiosError extends js.Object {

}

@js.native
trait AxiosData extends js.Object {


}

@js.native
trait AxiosResponse extends js.Object {

  val data:AxiosData = js.native

}
