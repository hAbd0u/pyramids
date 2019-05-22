package com.lyrx.pyramids.jszip

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSGlobal, JSGlobalScope}
import scala.scalajs.js.typedarray.ArrayBuffer



@JSGlobal
@js.native
class JSZip extends js.Object{


  def loadsy():String = js.native
}

@JSGlobal("JSZip")
@js.native
class JSZipp extends js.Object{


  def loadAsync(b:ArrayBuffer):Promise[Unit] = js.native
}


@js.native
trait JSPromise extends js.Any{


   def `then`(f:js.Function0[Unit])
}


trait Zipping{

}