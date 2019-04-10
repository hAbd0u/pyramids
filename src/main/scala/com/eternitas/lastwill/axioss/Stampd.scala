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
    ain.stampdApi=auth.stampdApi.getOrElse(null)
    ain.stampdApiSecret=auth.stampdApiSecret.getOrElse(null)
    ain
  }

  def authenticate(c: (AxiosResponse) => Unit, ec: (AxiosError) => Unit) = axios.
    get(url + "/init", l(
      "params" ->  l(
        "client_id" -> auth.stampdApi.getOrElse(null),
        "secret_key" -> auth.stampdApiSecret.getOrElse(null),
      )
    )).`then`(r=>c(r)).`catch`(thr=>ec(thr))





}
