package com.lyrx.pyramids.demo

trait ListDemo {
  val list1 = List(1, 2, 3)
  val list2 = List(4, 5, 6)

  def concat() =
    assert(List(1, 2, 3, 4, 5, 6) == (list1 ++ list2))

  def append() = assert(List(1,2,3,4)  == list1:+ 4)

  def foldLeft() = assert( list1.foldLeft(0)((a,b)=>a + b ) == 6)

  def tupels() =
    assert(
      ((t:(List[Int],List[Int]))  =>{ t._1.sum + t._2.sum}) ((list1,list2)) == 21)


}
