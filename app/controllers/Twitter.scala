//package controllers
//
//import Actors.TwitterParse
//import akka.actor.Props
//import play.api.libs.concurrent.Akka
//import play.api.mvc._
//
//
//import play.api.libs.oauth.ConsumerKey
//import play.api.libs.oauth.ServiceInfo
//import play.api.libs.oauth.OAuth
//import play.api.libs.oauth.RequestToken
//
//object Twitter extends Controller {
//
////  val twitterParse = Akka.system.actorOf(Props[TwitterParse], name = "twitterParse")
//
//  val KEY = ConsumerKey("TIoGIMVFdnmNcnm7A4hT1lIwR", "aIplGtCZ5sy0pSkrwqbjum2mDYHzlB5oDVWRAfVFhCP6d9YAoB")
//
//  val TWITTER = OAuth(ServiceInfo(
//    "https://api.twitter.com/oauth/request_token",
//    "https://api.twitter.com/oauth/access_token",
//    "https://api.twitter.com/oauth/authorize", KEY),
//    true)
//
//  def authenticate = Action { request =>
//
//    println("authenticate running: " + request)
//    request.getQueryString("oauth_verifier").map { verifier =>
//      val tokenPair = sessionTokenPair(request).get
//      // We got the verifier; now get the access token, store it and back to index
//      TWITTER.retrieveAccessToken(tokenPair, verifier) match {
//        case Right(t) => {
//          // We received the authorized tokens in the OAuth object - store it before we proceed
////          twitterParse ! request
//          Redirect(routes.Application.timeline).withSession("token" -> t.token, "secret" -> t.secret)
//
//        }
//        case Left(e) => throw e
//      }
//    }.getOrElse(
//        TWITTER.retrieveRequestToken("http://localhost:9000/auth") match {
//          case Right(t) => {
//            // We received the unauthorized tokens in the OAuth object - store it before we proceed
//            Redirect(TWITTER.redirectUrl(t.token)).withSession("token" -> t.token, "secret" -> t.secret)
//          }
//          case Left(e) => throw e
//        })
//  }
//
//  def sessionTokenPair(implicit request: RequestHeader): Option[RequestToken] = {
//    println("sessiontokenpair running: " + request)
//    for {
//      token <- request.session.get("token")
//      secret <- request.session.get("secret")
//    } yield {
//      RequestToken(token, secret)
//    }
//  }
//
//
//}
