package com.lyrx.pyramids.demo

trait NinetyNineProblems {

  val list1 = List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)



   class PimpedListList[T](ll:ListList[T]) {

     def matchList(b:T, aHead:List[T] , aTail:ListList[T])
                  (implicit comp:Equiv[T]) = aHead  match {
       case h  :: t => if(comp.equiv(h,b))
         (b +: aHead) +: aTail//same element again
       else
         List(b) +: ll   // new element, not the same as the one before
       case Nil => List(b) +: ll // new element
     }



    def collect(b:T)(implicit comp:Equiv[T]) = ll match {
      case (aHead:List[T]) :: (aTail:ListList[T]) => matchList(b,aHead,aTail)
      case Nil => List(List(b)) //first element ever
    }

  }


  def pack[T](l:List[T]) (implicit comp:Equiv[T])= l.foldLeft(
    Nil:ListList[T]) ( (ll,b) => (new PimpedListList(ll)).collect(b)).reverse

  def  compress[T](l:List[T]) (implicit comp:Equiv[T])= pack(l).
    map(l=>(l.length,l.head))

  def testPack()={
    println(pack(list1))
    println(compress(list1))
  }



}


object NinetyNineProblemsMain extends NinetyNineProblems {

  def main(args: Array[String]): Unit = {
    testPack()

  }
}