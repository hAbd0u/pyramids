package com.lyrx.pyramids.cryptography

import com.lyrx.pyramids.PyramidConfig

trait CanInstance[T] {
  def createInstance(config:PyramidConfig):T

  def config():PyramidConfig

  def msg(s:String) = createInstance(config().msg(s))


}
