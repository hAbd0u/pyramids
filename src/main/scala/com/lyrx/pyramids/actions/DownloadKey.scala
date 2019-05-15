package com.lyrx.pyramids.actions

import com.lyrx.pyramids.PyramidConfig

import scala.concurrent.{ExecutionContext, Future}

trait DownloadKey {

  val pyramidConfig: PyramidConfig



  def downloadKey()(implicit  executionContext: ExecutionContext):Future[PyramidConfig]={
    println(s"Pyramid: ${pyramidConfig}")
    Future{pyramidConfig}
  }

}
