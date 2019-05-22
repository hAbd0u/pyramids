package com.lyrx.pyramids

import org.scalajs.dom.raw.Blob

import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}
import scala.scalajs.js.|

package object jszip {
  type JSData = String  | ArrayBuffer | Blob | Uint8Array
}
