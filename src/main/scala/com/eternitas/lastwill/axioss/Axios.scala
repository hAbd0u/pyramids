package com.eternitas.lastwill.axioss

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

  def `then`(callback: js.Function1[AxiosImpl,js.Any]):AxiosImpl = js.native

  def `catch`(callback: js.Function1[AxiosError,js.Any]):AxiosImpl = js.native

  def post(address:String, data:PimpedFormData, headers:js.Dynamic):AxiosImpl = js.native


}


@js.native
trait AxiosError extends js.Object {

}
