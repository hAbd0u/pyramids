package com.lyrx.pyramids.temporal

import org.scalajs.dom.experimental.{Fetch, RequestInit, Response}
import org.scalajs.dom.ext.Ajax

import scalajs.js
import js.JSON
import js.Dynamic.{literal => l}
import scala.concurrent.Future
object Temporal {
  val DEV_LOGIN ="https://dev.api.temporal.cloud/v2/auth/login"


  def loginFetch(aUser:String, aPassword:String): Future[Response] = Fetch.fetch(
    DEV_LOGIN,
      l(
        "method" -> "POST",
        "headers" ->  l(
          "Content-Type" ->  "text/plain"
      ),
      "body" -> JSON.stringify(l(
        "username" -> aUser,
        "password" -> aPassword.toString()
      ))).asInstanceOf[RequestInit]
    ).toFuture



  def loginAjax (aUser:String, aPassword:String) = Ajax.
    post(
      url = DEV_LOGIN,
      data = JSON.stringify(l(
        "username" -> aUser,
        "password" -> aPassword.toString()
      )),
      headers = Map(
        "Content-Type" ->  "text/plain"),
      timeout = 0,
      withCredentials = false

    )



  // def handle()=login(null,null).toFuture





}
