package com.lyrx

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js
import scala.scalajs.js.|

package object pyramids {

  type TextFieldContents =
    js.UndefOr[java.lang.String | scala.Double | js.Array[java.lang.String]]

  implicit class FutureOption[T](val m: Future[Option[T]]) {

    def fmap[U](tf: T => U)(
        implicit executionContext: ExecutionContext): Future[Option[U]] = m.map(_.map(tf(_)))

    def fflatMap[U](tf: T => Future[Option[U]])(
        implicit executionContext: ExecutionContext): Future[Option[U]]= this.m
          .flatMap(_.map(tf(_)).getOrElse(Future { None }))


  }

}
