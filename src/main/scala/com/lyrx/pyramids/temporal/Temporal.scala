package com.lyrx.pyramids.temporal

import org.scalajs.dom.experimental.{Fetch, RequestInit, Response}

import scalajs.js
import js.JSON
import js.Dynamic.{literal => l}
import scala.concurrent.Future
object Temporal {


  def login (aUser:String,aPassword:String): Future[Response] = Fetch.fetch(
      "https://api.temporal.cloud/v2/auth/login",
      l(
        "method" -> "POST",
        "mode" -> "no-cors",
        "credentials" ->"include",
        "headers" ->  l(
          "Content-Type" ->  "text/plain"
      ),
      "body" -> JSON.stringify(l(
        "username" -> aUser,
        "password" -> aPassword.toString()
      ))).asInstanceOf[RequestInit]
    ).toFuture




  // def handle()=login(null,null).toFuture





}
