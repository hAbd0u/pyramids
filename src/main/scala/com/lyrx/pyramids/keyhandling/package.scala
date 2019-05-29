package com.lyrx.pyramids

import org.scalajs.dom.raw.{Blob, FileReader, ProgressEvent}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.typedarray.{ArrayBuffer, Uint8Array}

package object keyhandling {


type EncryptionResult =  (Option[ArrayBuffer], Option[ArrayBuffer], Option[ArrayBuffer], Option[ArrayBuffer])


}
