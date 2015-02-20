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



//Must have a class so the displayTimes function can be passed in. Usually these are objects which is why changes
//are required to global settings as can be found in app/Global.scala
class Analytics(dt: displayTimes) extends Controller {

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.JsonFormats._

  //use async to get a Result from Future[Result] without blocking, convert all rows to Json. The JsonFormat
  //defined in models.scala is used.

  def index = Action.async {
    dt.getAll.map(times => Ok(Json.toJson(times)))
  }
}
