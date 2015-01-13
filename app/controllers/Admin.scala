package controllers
import play.api._
import play.api.i18n.Messages
import play.api.mvc._
import play.api.i18n
import play.api.libs.json.Json
import play.api.libs.json.JsError

import models.SimpleClient
import models.displayTimes
import models.timeOnPage

import scala.concurrent.Future
import scala.util.Try


class Admin(dt: displayTimes) extends Controller {

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.JsonFormats._

  //not sure how to fix, template goes to homepage

  def index = Action.async {
    dt.getAll.map(times => Ok(Json.toJson(times)))
  }
}
