package com.lyrx.pyramids

import scala.concurrent.Promise
import scala.scalajs.js

package object ipfs {


  implicit class PimpedIpfs(ipfs:Ipfs){

    def onReadyOnce(c:js.Function0[Unit]) = ipfs.once("ready",c)


    def futureAdd(path:String,content:NodeBuffer) = {
      val promise = Promise[js.Array[IPFSSFile]]

      ipfs.add(path,content,(e,r)=>if(e != null )
          promise.failure(new Throwable(e.toString))
          else
          promise.success(r)
      )
       promise.future
    }

  }

}
