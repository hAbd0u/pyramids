package com.lyrx.pyramids

import scala.concurrent.Promise
import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.scalajs.js.typedarray.Uint8Array
package object ipfs {





  implicit class PimpedIpfsClient(ipfsClient:IpfsClient){


    def futureAdd(content:Buffer) = {
      val promise = Promise[js.Array[IPFSSFile]]

      ipfsClient.add(content,l(),(e,r)=>if(e != null )
        promise.failure(new Throwable(e.toString))
      else
        promise.success(r)
      )
      promise.future
    }

  }




}
