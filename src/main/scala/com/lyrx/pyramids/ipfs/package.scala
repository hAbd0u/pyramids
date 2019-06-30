package com.lyrx.pyramids

import typings.{nodeLib, stdLib}
import typings.stdLib.{ArrayBuffer, Uint8Array}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.scalajs.js
import js.Dynamic.{literal => l}

import js.|
import typings.nodeLib.bufferMod

import scala.language.implicitConversions

package object ipfs {

  type CanBuffer = String | ArrayBuffer | Uint8Array

  type OrError = js.UndefOr[js.Error]
  type ErrorPromise = js.Promise[OrError]
  type ErrorCallback = js.Function1[OrError, Unit]

  type PubSubHandler = js.Function1[PubSubMessage, Unit]

  implicit def typedArrayArrayBufferToStdlib(
      b: js.typedarray.ArrayBuffer): stdLib.ArrayBuffer =
    b.asInstanceOf[stdLib.ArrayBuffer]
  implicit def jstdLibArrayBufferToTypedArrayArrayBuffer(
      b: js.typedarray.ArrayBuffer): js.typedarray.ArrayBuffer =
    b.asInstanceOf[js.typedarray.ArrayBuffer]


  implicit class PimpedString(b: js.typedarray.ArrayBuffer) {

    def myToString() = new TextDecoder().decode(new js.typedarray.Uint8Array(b))
  }

  implicit class PimpedIpfsClient(val ipfsClient: IpfsClient)
      extends PubSubSupport
      with PinSupport {

    def futureCat(s: String): Future[nodeLib.Buffer] =
      ipfsClient.cat(s).toFuture

    def futureCatString(s: String) = ipfsClient.catString(s).toFuture

    def futureAddString(s: String) = futureAdd(bufferMod.Buffer.from(s))

    def futurePin(s: String)(implicit executionContext: ExecutionContext) =
      futureAddString(s).flatMap(
        _.headOption
          .map(r => pinAdd(r.hash).map(r2 => Some(r2)))
          .getOrElse(Future { None }))

    def futureAdd(content: nodeLib.Buffer) = {
      val promise = Promise[js.Array[IPFSSFile]]

      ipfsClient.add(content,
                     l(),
                     (e, r) =>
                       if (e != null)
                         promise.failure(new Throwable(e.toString))
                       else
                         promise.success(r))
      promise.future
    }

  }

}
