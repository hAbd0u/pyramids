package com.lyrx.pyramids.demo

import scala.annotation.tailrec

trait ListDemo2 {

  val list1 = List(1, 2, 3)
  val list2 = List(4, 5, 6)

  val list3 =List("a","a","b","b","b","c","c")


  /*

            1, A,B,C,D, E,  ..............   Z
            A,B,C,D, E,  ..............   Z, 1

            List("c","b","a").reverse

            List("a","b","c")




   */
  @tailrec
  final def compress[T](l:List[T],collector:List[T]  = Nil):List[T] = l match {
    case Nil          => Nil
    case (h :: atail) =>   compress(atail.dropWhile(_ == h),    collector :+ h )
  }









  //@tailrec
  final def compressTailRec[T](
                                l:List[T],
                                collector:List[T]=Nil):List[T] =  ???


  final def compressTailRec2[T](
                                 l:List[T],
                                 collector:List[T]=Nil):List[T] = {
    @tailrec
    def collect(ll: List[T], ccollector: List[T] = Nil): List[T] =
      ll match {
        case Nil          => ccollector
        case (h :: atail) => collect(atail.dropWhile(_ == h), h +: ccollector)
      }

    collect(l, collector).reverse
  }



  def compressTest() = ???

}
