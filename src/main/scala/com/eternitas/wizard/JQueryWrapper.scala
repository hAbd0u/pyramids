package com.eternitas.wizard

import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.jquery.{JQuery, JQueryXHR}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.|


//See JQueryStatic
@js.native
@JSGlobal
class JQueryWrapper extends js.Object {
  def apply(): JQuery = js.native
  def apply(selector: String): JQuery = js.native
  def apply(selector: String, context: Element | JQuery): JQuery = js.native



  // Add a comment
  /**
    * Creates DOM elements on the fly from the provided string of raw HTML.
    *
    * Note that the HTML-only signature works, but happens to match one of the above cases. If the
    * contents of "selector" happens to contain HTML, it creates instead of matching.
    */
  def apply(html:String, ownerDocument:dom.html.Document):JQuery = js.native
  def apply(html:String, attributes:js.Dictionary[js.Any]):JQuery = js.native
  def apply(obj: js.Object): JQuery = js.native

  /**
    * Binds a function to be executed when the DOM has finished loading.
    */
  def apply(func: js.Function): JQuery = js.native

  def get(
           url:String,
           data:String | js.Object = ???,
           success:js.Function3[js.Object, String, JQueryXHR, Any] = ???,
           dataType:String = ???):JQueryXHR = js.native


}
