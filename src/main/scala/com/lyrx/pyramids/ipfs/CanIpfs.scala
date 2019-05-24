package com.lyrx.pyramids.ipfs

import com.lyrx.pyramids.{CanInstantiate, PyramidConfig}

import scala.scalajs.js
import js.Dynamic.{literal => l}
import scala.scalajs.js.Math
trait CanIpfs //extends CanInstantiate
{
  val pyramidConfig:PyramidConfig


  def initIpfs() ={
    //instantiate[Ipfs](l("repo"  -> s"ipfs-${Math.random().toString()}" )).
    new Ipfs(l("repo"  -> s"ipfs-${Math.random().toString()}" )).
      onReadyOnce(()=>
      println("IPFS object initialized: ")
    )
  }

}
