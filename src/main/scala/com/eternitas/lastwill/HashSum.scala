package com.eternitas.lastwill

import org.scalajs.dom.crypto.{BufferSource, _}

import scala.scalajs.js

object HashSum {

  def hash(b: BufferSource): js.Promise[js.Any] =
    crypto.subtle.digest("SHA-256", b)

}
