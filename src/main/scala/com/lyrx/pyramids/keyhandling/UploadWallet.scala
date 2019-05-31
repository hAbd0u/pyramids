package com.lyrx.pyramids.keyhandling

import com.lyrx.pyramids.PyramidConfig
import com.lyrx.pyramids.pcrypto.WalletNative
import org.scalajs.dom.File
import org.scalajs.dom.raw.{Blob, EventTarget, FileReader}
import typings.jqueryLib.JQueryEventObject

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.UndefOr

import com.lyrx.pyramids.pcrypto.PCryptoImplicits._

@js.native
trait DataTransferTarget extends EventTarget {


  val files:js.UndefOr[js.Array[Blob]] = js.native

}


@js.native
trait DataTransferEvent extends JQueryEventObject {

  val dataTransfer:UndefOr[DataTransferTarget] = js.native

  //val originalEvent:UndefOr[DataTransferEvent]=js.native

}


trait UploadWallet extends KeyImport {


  def uploadWallet(f:File)(implicit executionContext: ExecutionContext) = if (
    f.`type` == "application/json")
      new FileReader().futureReadArrayBuffer(f).
        map(arrayBuffer =>js.JSON.parse(arrayBuffer.toNormalString()).asInstanceOf[WalletNative]).
        flatMap(walletNative => importAllKeys(walletNative).
          map(_.pyramidConfig.msg("Oh Pharao, we have imported your keys!")))
    else
      Future{pyramidConfig.error("Oh Pharao, sorry, we cannot import this data format!")}








}
