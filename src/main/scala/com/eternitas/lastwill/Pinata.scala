package com.eternitas.lastwill

import scala.scalajs.js
import js.Dynamic.{literal => l}
import com.eternitas.lastwill.axioss.{AxiosError, AxiosImpl, axios}
import org.scalajs.dom.raw.FormData

class Pinata(auth: PinataAuth) {
  val url = "https://api.pinata.cloud/data/testAuthentication";

  def authenticate(c:(AxiosImpl) => Unit,ec:(AxiosError)=>Unit) = axios
      .get(url,
           l(
             "headers" -> l(
               "pinata_api_key" -> auth.api,
               "pinata_secret_api_key" -> auth.secretApi
             )))
      .`then`(a => c(a))
      .`catch`(e => ec(e))



  new FormData()

}
