package com.eternitas.lastwill.cryptoo



import org.scalajs.dom.crypto
import org.scalajs.dom.crypto.BufferSource

import scala.scalajs.js

object HashSum {

  def hash(b: BufferSource): js.Promise[js.Any] =
    crypto.crypto.subtle.digest("SHA-256", b)

}
