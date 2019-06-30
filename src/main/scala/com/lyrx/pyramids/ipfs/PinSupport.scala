package com.lyrx.pyramids.ipfs

import scala.concurrent.Future
import scala.scalajs.js

trait PinSupport {
  val ipfsClient: IpfsClient

  def pinAdd(h: String): Future[js.Array[PinResult]] =
    ipfsClient.pin.add(h).toFuture
}