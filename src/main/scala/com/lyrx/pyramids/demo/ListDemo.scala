package com.lyrx.pyramids.demo

trait ListDemo {
  val list1 = List(1, 2, 3)
  val list2 = List(4, 5, 6)

  def concat() =
    assert(List(1, 2, 3, 4, 5, 6) == (list1 ++ list2))

  def append() = assert(List(1,2,3,4)  == list1:+ 4)

  def foldLeft() = assert( list1.foldLeft(0)((a,b)=>a + b ) == 6)



  def tupels1() = {
    val f: ((List[Int], List[Int])) => Int = (t: (List[Int], List[Int])) => {
      t._1.sum + t._2.sum
    }
    assert(f ((list1, list2)) == 21)
  }

  def tupels2() = {
    type T = (List[Int], List[Int])
    type TF = T => Int

    val f: TF  = t => t._1.sum + t._2.sum

    val at = (list1, list2)
    assert(f (at) == 21)
  }




  def tupels3() =
    assert(
      ((t:(List[Int],List[Int]))  =>{ t._1.sum + t._2.sum}) ((list1,list2)) == 21)


}
