package com.lyrx.pyramids.demo

import scala.annotation.tailrec

object ListDemo {

  val list1 = List(1, 2, 3)
  val list2 = List(4, 5, 6)
  val list3 = List("a", "a", "b", "b", "b", "c", "c")

  implicit class PimpedList[T](l: List[T]) {

    def compress() = compressTailRec2(l)

  }

  def concat() =
    assert(List(1, 2, 3, 4, 5, 6) == (list1 ++ list2))

  def append() = assert(List(1, 2, 3, 4) == list1 :+ 4)

  def foldLeft() = assert(list1.foldLeft(0)((a, b) => a + b) == 6)

  def tupels1() = {
    val f: ((List[Int], List[Int])) => Int = (t: (List[Int], List[Int])) => {
      t._1.sum + t._2.sum
    }
    assert(f((list1, list2)) == 21)
  }

  def tupels2() = {
    type T = (List[Int], List[Int])
    type TF = T => Int

    val f: TF = t => t._1.sum + t._2.sum

    val at = (list1, list2)
    assert(f(at) == 21)
  }

  def tupels3() =
    assert(((t: (List[Int], List[Int])) => { t._1.sum + t._2.sum })(
      (list1, list2)) == 21)

  final def compress[T](l: List[T]): List[T] = l match {
    case Nil          => Nil
    case (h :: atail) => h +: compress(atail.dropWhile(_ == h))
  }
  @tailrec
  final def compressTailRec[T](l: List[T], collector: List[T] = Nil): List[T] =
    l match {
      case Nil => collector
      case (h :: atail) =>
        compressTailRec(atail.dropWhile(_ == h), collector :+ h)
    }

  final def compressTailRec2[T](l: List[T],
                                collector: List[T] = Nil): List[T] = {
    @tailrec
    def collect(ll: List[T], ccollector: List[T] = Nil): List[T] =
      ll match {
        case Nil          => ccollector
        case (h :: atail) => collect(atail.dropWhile(_ == h), h +: ccollector)
      }

    collect(l, collector).reverse
  }

  // val list3 = List("a", "a", "b", "b", "b", "c", "c")

  def compressTest() = {
    val result = List("a", "b", "c")
    assert(compress(list3) == result)
    assert(compressTailRec(list3) == result)
    assert(compressTailRec2(list3) == result)
    assert(list3.compress() == result) //implicit!!!
  }

  def main(args: Array[String]): Unit = {

    concat()
    append()
    foldLeft()
    tupels1()
    tupels2()
    tupels3()
    compressTest()

  }

}
