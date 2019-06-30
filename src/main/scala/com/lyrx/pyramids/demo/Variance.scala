package com.lyrx.pyramids.demo


/*
https://medium.com/@wiemzin/variances-in-scala-9c7d17af9dc4
 */
object Variance {


  // C --> B --> A
  class A
  val a = new A()
  class B extends A
  val b = new B()
  class C extends B
  val c = new C()
  class D
  val d = new D()

  class C1[T <: B](p:T) // T needs to be a subtype of B
  //val  c11 = new C1(a)
  val c12 = new C1(b)
  val c13 = new C1(c)
  //val c14 = new C1(d)

  class C2[T >: B](p:T)   // B is  always a subtype of T, as T is scala.Any
  val  c21 = new C2(a)
  val c22 = new C2(b)
  val c23 = new C2(c)
  val c24 = new C2(d)

  class C3[+T](p:T)  // Covariance:      A subtype of T ==> C3[A]  subtype of C3[T]
  type C3A =C3[A]
  type C3B =C3[B]   // C3B is subtype of C3A

  class C32[T <: C3A](p:T)
  new C32(new C3(a))
  new C32(new C3(b))
  new C32(new C3(c))
  //new C32(new C3(d))
  class C33[T >: C3A](p:T)



  class C4[-T](p:T)  // Contravariance:  T supertype of A ==> C4[T]  supertype of C4[A]
  type C4A =C4[A]
  type C4B =C4[B]   // C4A is supertype of C4B

  class C42[T <: C4A](p:T)
  new C42(new C4(a))
  //new C42(new C4(b))
  //new C42(new C4(c))
  //new C42(new C4(d))



  class C5[T](p:T)   // Invariance:   A related to T ==>   C5[A] not related to C5[T]
  type C5A =C5[A]
  type C5B =C5[B]   // C5B is NOT subtype of C5A




  def main(args: Array[String]): Unit = {

  }

}
