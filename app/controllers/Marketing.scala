package controllers

import play.api._

import play.api.mvc._

object Marketing extends Controller {
  def about = Action {
    Ok(views.html.marketing.about("About us"))
  }
}