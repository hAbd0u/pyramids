package com.eternitas.lastwill.axioss

import com.eternitas.lastwill.AllCredentials
import org.scalajs.dom.raw.{Blob, File, FormData, HTMLFormElement}

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l}
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.ArrayBuffer

@js.native
trait StampdData extends AxiosData {


}


@js.native
trait StampdResponse extends AxiosResponse {


}




class Stampd(auth: AllCredentials) {
  val url = "https://stampd.io/api/v2";

  def export(ain:js.Dynamic): js.Dynamic = {
    ain.stampdApi=auth.stampdApi
    ain.stampdApiSecret=auth.stampdApiSecret
    ain
  }

  def authenticate(c: (AxiosResponse) => Unit, ec: (AxiosError) => Unit) = axios.
    get(url + "/init", l(
      "params" ->  l(
        "client_id" -> auth.stampdApi,
        "secret_key" -> auth.stampdApiSecret,
      )
    )).`then`(r=>c(r)).`catch`(thr=>ec(thr))





}
