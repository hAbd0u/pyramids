package com.lyrx.pyramids.temporal

import com.lyrx.pyramids.ipfs.CanIpfs
import org.scalajs.dom.experimental.{Fetch, RequestInit, Response}
import org.scalajs.dom.ext.Ajax

import scalajs.js
import js.JSON
import js.Dynamic.{literal => l}
import scala.concurrent.{ExecutionContext, Future}




/*

{
"expire":"2019-06-14T11:35:30Z",
"token":"eyJhg"}

 */


@js.native
trait JWTToken extends js.Object {
  val expire:String = js.native
  val token:String = js.native
}


@js.native
trait TemporalCredentials extends js.Object {

  val username:String=js.native
  val password:String=js.native
}


trait Temporal extends CanIpfs{
  val DEV_LOGIN ="https://dev.api.temporal.cloud/v2/auth/login"

  implicit class PimpedTemporalCredentials(temporalCredentials: TemporalCredentials){
    def loginFetch() = Fetch.fetch(
      DEV_LOGIN,
      l(
        "method" -> "POST",
        "headers" ->  l(
          "Content-Type" ->  "text/plain"
        ),
        "body" -> JSON.stringify(l(
          "username" -> temporalCredentials.username,
          "password" -> temporalCredentials.password
        ))).asInstanceOf[RequestInit]
    ).toFuture



    def loginAjax () = Ajax.
      post(
        url = DEV_LOGIN,
        data = JSON.stringify(l(
          "username" -> temporalCredentials.username,
          "password" -> temporalCredentials.password
        )),
        headers = Map(
          "Content-Type" ->  "text/plain"),
        timeout = 0,
        withCredentials = false

      )

    def pinJSW()(implicit executionContext: ExecutionContext) = loginAjax().
      map(_.responseText)

  }
  def jwtToken()(implicit executionContext: ExecutionContext)=
    pyramidConfig.temporalOpt.
      map(t=>
        t.loginAjax().map(r =>
          Some(JSON.parse(
            r.responseText).asInstanceOf[JWTToken]))
      ).getOrElse(Future{None})


}
