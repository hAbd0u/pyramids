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
      implicit executionContext: ExecutionContext): Future[Pyramid] = ??? // pyramidConfig.temporalCredentialsOpt





    /*
    Future {
    new Pyramid(
      pyramidConfig
        .copy(
          ipfsOpt = Some(IpfsHttpClient(l(
            "host" -> "dev.api.ipfs.temporal.cloud",
            "port" -> 443,
            "protocol" -> "https",
            "api-path" -> "/api/v0/",
            "protocol" -> "https",
            "headers" -> l(
              "authorization" -> s"Bearer ${jwt.token}"
            )
          )))
        )
        .msg("Connected to the Temporal IPFS network!"))
  }

     */



}
