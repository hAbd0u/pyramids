package com.lyrx.pyramids.macmini

import com.lyrx.pyramids.Pyramid
import com.lyrx.pyramids.ipfs.{IpfsHttpClient, IpfsProxy}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => l}


trait MacminiProxy  extends IpfsProxy{

  override def initIpfs()(implicit executionContext: ExecutionContext):Future[Pyramid] = Future {
    new Pyramid(
      pyramidConfig
        .copy(
          infuraClientOpt = Some(
            IpfsHttpClient(
              l(
                "host" -> "192.168.1.30",
                "port" -> 5001,
                "protocol" -> "http"
              )))
        )
        .msg("Connected to the macmini IPFS " +
          "node"))
  }

}
