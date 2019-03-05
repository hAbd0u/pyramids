package com.eternitas.wizard

import org.scalajs.dom.raw.{Storage, WindowLocalStorage}

import scala.scalajs.js

object Bookmark{


  def localStorage():PimpedLocalStorage = org.scalajs.dom.window.localStorage


   implicit class PimpedLocalStorage(localStorage:Storage){
     private val KEY   = "com.lyrx.bookmark.wizard"

     def getKey(bookName:String):String=s"${KEY}.${bookName}"

     def safeScreen(index:Int,bookName:String)={
       localStorage.setItem(getKey(bookName),s"${index}")

     }
     def lastScreen(bookName:String) ={
       val bookmark = localStorage.getItem(getKey(bookName))
       if (js.isUndefined(bookmark) || bookmark == null) None
         else Some(bookmark.toInt)
     }

  }
}


class Bookmark {

}
