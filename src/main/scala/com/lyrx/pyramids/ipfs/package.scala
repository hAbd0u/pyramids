package com.lyrx.pyramids

import scala.concurrent.Promise
import scala.scalajs.js
import js.Dynamic.{literal => l}
package object ipfs {


  implicit class PimpedIpfs(ipfs:Ipfs){

    def onReadyOnce(c:js.Function0[Unit]) = ipfs.once("ready",c)


    def futureAdd(content:NodeBuffer) = {
      val promise = Promise[js.Array[IPFSSFile]]

      ipfs.add(content,l(),(e,r)=>if(e != null )
          promise.failure(new Throwable(e.toString))
          else
          promise.success(r)
      )
       promise.future
    }

  }

}
