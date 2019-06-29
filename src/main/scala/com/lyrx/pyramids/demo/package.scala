package com.lyrx.pyramids

package object demo {

  type ListList[T]  = List[List[T]]



  implicit class PimpedList[T](l:List[T]) extends ListDemo{

    def compress() = compressTailRec2(l)

  }

}
