package com.lyrx.pyramids.infura

import com.lyrx.pyramids.{Pyramid, PyramidConfig}
import com.lyrx.pyramids.ipfs.IpfsHttpClient

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import js.Dynamic.{literal => l}




trait InfuraProxy {
  val pyramidConfig:PyramidConfig

  def initIpfs()(implicit executionContext: ExecutionContext):Future[Pyramid] = Future {
    new Pyramid(
      pyramidConfig
        .copy(
          ipfsOpt = Some(
            IpfsHttpClient(
              l(
                "host" -> "ipfs.infura.io",
                "port" -> 5001,
                "protocol" -> "https"
              )))
        )
        .msg("Connected to IPFS network!"))
  }

}
