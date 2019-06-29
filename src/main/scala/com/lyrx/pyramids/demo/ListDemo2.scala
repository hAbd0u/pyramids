package com.lyrx.pyramids.demo

import scala.annotation.tailrec

trait ListDemo2 {

  val list1 = List(1, 2, 3)
  val list2 = List(4, 5, 6)

  val list3 =List("a","a","b","b","b","c","c")


  final def compress[T](l:List[T]):List[T] =  ???



  //@tailrec
  final def compressTailRec[T](
                                l:List[T],
                                collector:List[T]=Nil):List[T] =  ???


  final def compressTailRec2[T](
                                 l:List[T],
                                 collector:List[T]=Nil):List[T] = ???



  def compressTest() = ???

}
