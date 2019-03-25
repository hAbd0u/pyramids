package com.eternitas.lastwill

import scala.scalajs.js

import js.Dynamic.{literal => l}

import com.eternitas.lastwill.axioss.axios

class Pinata(auth: PinataAuth) {
  val url = "https://api.pinata.cloud/data/testAuthentication";

  def authenticate() = axios
      .get(url,
           l(
             "headers" -> l(
               "pinata_api_key" -> auth.api,
               "pinata_secret_api_key" -> auth.secretApi
             )))
      .`then`(r => {})
      .`catch`(e => {})



}
