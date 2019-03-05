package com.eternitas.wizard

import org.querki.jquery.JQuery

trait Content {
  def show(wasGood:(JQuery)=>Unit)(implicit $ : JQueryWrapper): JQuery
}
