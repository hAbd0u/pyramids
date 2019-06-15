package com.lyrx.pyramids.temporal

import com.lyrx.pyramids.ipfs.{IpfsHttpClient, IpfsProxy}
import com.lyrx.pyramids.{Pyramid, PyramidConfig}

import scala.concurrent.{ExecutionContext, Future}
import scalajs.js
import js.Dynamic.{literal => l}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
trait TemporalProxy extends IpfsProxy {



  override def initIpfs()(
      implicit executionContext: ExecutionContext): Future[Pyramid] =  {
    pyramidConfig.temporalData.
      temporalJWTOpt.map(jwt=>Future {
      new Pyramid(
        pyramidConfig
          .withTemporalClient(
            IpfsHttpClient(l(
              "host" -> "dev.api.ipfs.temporal.cloud",
              "port" -> 443,
              "protocol" -> "https",
              "api-path" -> "/api/v0/",
              "headers" -> l(
                "authorization" -> s"Bearer ${jwt.token}"
              )
            ))
          )
          .msg("Prepared for the Temporal IPFS network!"))}).getOrElse(Future{
      new Pyramid(pyramidConfig.msg("No JWT-Token!") )
    })
  }








}
