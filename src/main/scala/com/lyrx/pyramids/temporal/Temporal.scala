package com.lyrx.pyramids.temporal

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import com.lyrx.pyramids.ipfs.{CanIpfs, IpfsClient, PinResult}
import org.scalajs.dom.experimental.{Fetch, RequestInit, Response}
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData
import typings.nodeLib

import scalajs.js
import js.JSON
import js.Dynamic.{literal => l}
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.typedarray.Uint8Array

/*

{
"expire":"2019-06-14T11:35:30Z",
"token":"eyJhg"}

 */
@js.native
trait JWTToken extends js.Object {
  val expire: String = js.native
  val token: String = js.native
}
@js.native
trait TemporalCredentials extends js.Object {

  val username: String = js.native
  val password: String = js.native
}

class TemporalProxyImpl(override val pyramidConfig: PyramidConfig)
    extends TemporalProxy {}

trait Temporal {
  val DEV_LOGIN = "https://dev.api.temporal.cloud/v2/auth/login"

  val pyramidConfig: PyramidConfig

  def proxy(pc: PyramidConfig) = new TemporalProxyImpl(pc)

  implicit class PimpedTemporalCredentials(
      temporalCredentials: TemporalCredentials) {
    def loginFetch() =
      Fetch
        .fetch(
          DEV_LOGIN,
          l(
            "method" -> "POST",
            "headers" -> l(
              "Content-Type" -> "text/plain"
            ),
            "body" -> JSON.stringify(
              l(
                "username" -> temporalCredentials.username,
                "password" -> temporalCredentials.password
              ))
          ).asInstanceOf[RequestInit]
        )
        .toFuture

    def loginAjax() = Ajax.post(
      url = DEV_LOGIN,
      data = JSON.stringify(
        l(
          "username" -> temporalCredentials.username,
          "password" -> temporalCredentials.password
        )),
      headers = Map("Content-Type" -> "text/plain"),
      timeout = 0,
      withCredentials = false
    )

    def pinJSW()(implicit executionContext: ExecutionContext) =
      loginAjax().map(_.responseText)

  }

  def configFromLyrx()(implicit executionContext: ExecutionContext) =
    Fetch
      .fetch("http://data.lyrx.de/jwt.txt",
             l(
               ).asInstanceOf[RequestInit])
      .toFuture
      .flatMap(r => r.text().toFuture)
      .map(s => pyramidConfig.withTemporal(s))

  def jwtToken()(implicit executionContext: ExecutionContext) =
    pyramidConfig.temporalData.temporalCredentialsOpt
      .map(
        t =>
          t.loginAjax()
            .map(r => Some(JSON.parse(r.responseText).asInstanceOf[JWTToken])))
      .getOrElse(Future { None })

  def jwtTokenFromInfura()(implicit executionContext: ExecutionContext) =
    pyramidConfig.ipfsData.temporalOpt
      .map(
        hash => pyramidConfig.infuraClientOpt
            .map( (ipfs:IpfsClient) =>ipfs
                .futureCatString(hash)
                .map((s: String) => Some(JSON.parse(s).asInstanceOf[JWTToken])))
            .getOrElse(Future { None }))
      .getOrElse(Future { None })
      .map(_.map(t =>
        pyramidConfig.withTemporalJWT(t).msg("We have your JWT Token"))
        .getOrElse(pyramidConfig.msg("No JWT Token, sorry!")))

  def initTemporal()(implicit executionContext: ExecutionContext) = //Future{new Pyramid(pyramidConfig)}
    jwtTokenFromInfura().flatMap(proxy(_).initIpfs())

  def send()(implicit executionContext: ExecutionContext) =
    pyramidConfig.temporalData.temporalClientOpt
      .map(c => c.pubsubPublish("pharaoh","Test").map(r => pyramidConfig))
      .getOrElse(Future { pyramidConfig })

  def pinJWTToken()(implicit executionContext: ExecutionContext) =
    jwtToken().flatMap(
      _.flatMap(jwt =>
        pyramidConfig.infuraClientOpt.map(_.futurePin(JSON.stringify(jwt))))
        .getOrElse(Future { None }
        ))

}
