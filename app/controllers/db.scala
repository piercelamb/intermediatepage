//package controllers
//
//import play.api.data.Form
//import play.api.data.Forms._
//import play.api.mvc.{Action, Controller}
//
//import anorm._
//
//import controllers.routes._
//
//object db extends Controller {
//
//
////  val DBForm = Form {
////    mapping(
////      "id" -> ignored(None:Option[Long]),
////      "city" -> text,
////      "regionName" -> text,
////      "country" -> text,
////      "firstName" -> text,
////    )
////  }
//
//  /** Alter the login page action to suit your application. */
//  def index = Action { Redirect(auth.routes.Landing.db(0, 2, "")) }
//
//
//}
