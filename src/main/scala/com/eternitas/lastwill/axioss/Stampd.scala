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
  val url = "";

  def export(ain:js.Dynamic): js.Dynamic = {
    ain.stampdApi=auth.stampdApi
    ain.stampdApiSecret=auth.stampdApiSecret
    ain
  }

  def authenticate(c: (AxiosResponse) => Unit, ec: (AxiosError) => Unit) = {

  }




}
