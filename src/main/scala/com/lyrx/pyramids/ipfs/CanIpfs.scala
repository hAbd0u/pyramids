package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.{CanInstantiate, Pyramid, PyramidConfig}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.concurrent.{ExecutionContext, Promise}
import scala.scalajs.js.Math
trait CanIpfs
{
  val pyramidConfig:PyramidConfig


  def initIpfs()(implicit executionContext: ExecutionContext) ={
    val promise:Promise[Ipfs] = Promise[Ipfs]
    val ipfs =new Ipfs(l("repo"  -> s"ipfs-${Math.random().toString()}" ))
    ipfs.
      onReadyOnce(()=>promise.success(ipfs)
    )
    promise.future.map(i=>new Pyramid(pyramidConfig.copy(ipfsOpt = Some(ipfs))))
  }

}
