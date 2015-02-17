//package Utils
//
//import Actors.TwitterParse
//import akka.actor.Props
//import play.api.Play.current
//import play.api.libs.concurrent.Akka
//import play.api.mvc._
//
//
//import play.api.libs.oauth.ConsumerKey
//import play.api.libs.oauth.ServiceInfo
//import play.api.libs.oauth.OAuth
//import play.api.libs.oauth.RequestToken
//
//object Twitter_oauth {
//
//  val KEY = ConsumerKey(current.configuration.getString("twitter.consumer.key").get, current.configuration.getString("twitter.consumer.secret").get)
//
//  val TWITTER = OAuth(ServiceInfo(
//    "https://api.twitter.com/oauth/request_token",
//    "https://api.twitter.com/oauth/access_token",
//    "https://api.twitter.com/oauth/authorize", KEY),
//  true)
//
//  def authenticate = {
//
//  }
//}
