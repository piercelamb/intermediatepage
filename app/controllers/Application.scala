//package controllers
//
//import play.api.Play.current
//import play.api.libs.oauth.OAuthCalculator
//import play.api.libs.ws.WS
//import play.api.mvc._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//import scala.concurrent.Future
//
////
////object Application extends Controller {
////
////
////
////  def timeline = Action.async { implicit request =>
////    Twitter.sessionTokenPair match {
////      case Some(credentials) => {
////        WS.url("https://api.twitter.com/1.1/users/search.json?q=pierce%20lamb&page=1&count=3")
////          .sign(OAuthCalculator(Twitter.KEY, credentials))
////          .get
////          .map(result => Ok(result.json))
////      }
////
////    }
////  }
////
////}
