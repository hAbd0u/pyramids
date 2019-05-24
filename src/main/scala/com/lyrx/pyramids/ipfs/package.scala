package com.lyrx.pyramids

import scala.scalajs.js

package object ipfs {


  implicit class PimpedIpfs(ipfs:Ipfs){

    def onReadyOnce(c:js.Function0[Unit]) = ipfs.once("ready",c)
  }

}
