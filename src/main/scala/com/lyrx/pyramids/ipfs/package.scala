package com.lyrx.pyramids


import typings.nodeLib
import typings.stdLib.{ArrayBuffer, Uint8Array}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.scalajs.js.|
package object ipfs {


  type CanBuffer = String | ArrayBuffer | Uint8Array





  implicit class PimpedIpfsClient(ipfsClient:IpfsClient){


    def pinAdd(h:String):Future[PinResult] = ipfsClient.pin.add(h).toFuture

    def futureCat(s:String) = ipfsClient.cat(s).toFuture

    def futureCatString(s:String) = ipfsClient.catString(s).toFuture

    def futureAdd(content:nodeLib.Buffer) = {
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
