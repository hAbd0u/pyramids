package com.lyrx.pyramids


import typings.{nodeLib, stdLib}
import typings.stdLib.{ArrayBuffer, Uint8Array}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.scalajs.js.|
import typings.nodeLib.bufferMod

import scala.language.implicitConversions
package object ipfs {


  type CanBuffer = String | ArrayBuffer | Uint8Array


  type OrError = js.UndefOr[js.Error]
  type ErrorPromise = js.Promise[OrError]
  type ErrorCallback = js.Function1[OrError,Unit]

  type PubSubHandler = js.Function1[PubSubMessage,Unit]


  implicit def typedArrayArrayBufferToStdlib(b:js.typedarray.ArrayBuffer):stdLib.ArrayBuffer = b.asInstanceOf[stdLib.ArrayBuffer]
  implicit def jstdLibArrayBufferToTypedArrayArrayBuffer(b:js.typedarray.ArrayBuffer):js.typedarray.ArrayBuffer = b.asInstanceOf[js.typedarray.ArrayBuffer]

  trait PubSubSupport{
    val ipfsClient:IpfsClient

    def pubsubPublish(topic:String,message:String): Future[OrError] = ipfsClient.pubsub.publish(topic,bufferMod.Buffer.from(message)).toFuture

    def pubsubSubscribe(topic:String,h:PubSubHandler): Future[OrError] = ipfsClient.pubsub.subscribe(topic,h).toFuture

    def pubsubUnsubscribe(topic:String,h:PubSubHandler): Future[OrError] = ipfsClient.pubsub.unsubscribe(topic,h).toFuture

    def pubsubLs(): Future[js.Array[String]] = ipfsClient.pubsub.ls().toFuture

    def pubsubPeers(topic:String): Future[js.Array[String]] =ipfsClient.pubsub.peers(topic).toFuture
  }


  trait PinSupport {
    val ipfsClient: IpfsClient

    def pinAdd(h:String): Future[js.Array[PinResult]] = ipfsClient.pin.add(h).toFuture
  }


  implicit class PimpedIpfsClient(val ipfsClient:IpfsClient) extends  PubSubSupport  with PinSupport {


    def futureCat(s:String) = ipfsClient.cat(s).toFuture

    def futureCatString(s:String) = ipfsClient.catString(s).toFuture


    def futureAddString(s:String) = futureAdd(bufferMod.Buffer.from(s))

    def futurePin(s:String)(implicit executionContext: ExecutionContext) = futureAddString(s).
      flatMap(_.headOption.map(r =>pinAdd(r.hash).map(r2=>Some(r2))).getOrElse(Future{None}))


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
