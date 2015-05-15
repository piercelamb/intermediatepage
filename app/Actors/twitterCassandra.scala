package Actors

import akka.actor.Actor
import models.SimpleClient
import play.api.Play._
import play.api.libs.json
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.oauth.{OAuthCalculator, RequestToken, ConsumerKey}
import play.api.libs.ws.WS
import play.libs.Json
import scala.collection.JavaConversions.setAsJavaSet
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by plamb on 2/27/15.
 */
class twitterCassandra(client: SimpleClient) extends Actor {

  //credentials needed for twitter
  val KEY = ConsumerKey(current.configuration.getString("twitter.consumer.key").get, current.configuration.getString("twitter.consumer.secret").get)
  val TOKEN = RequestToken(current.configuration.getString("twitter.access.token").get, current.configuration.getString("twitter.access.secret").get)

  private var buildURL: String = _
  private val urlBoilerPlate = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name="
  private val parameters = "&count=190&trim_user=true"



  def receive = {
    case screenName: String => {

        buildURL = urlBoilerPlate + screenName + parameters

      println("Build URL for cassandra = " +buildURL)

      val result = WS.url(buildURL)
        .sign(OAuthCalculator(KEY, TOKEN))
        .get
        .map(result => (result.json \\ "text").map(_.validate[String]))

      val parseResult = Await.result(result, 5 seconds)

      val finalResult = parseResult.map(JsSuccess => JsSuccess.get).toSet

      println("twitter Actor received, sending: " +screenName)
      client.insertTwitter(screenName, finalResult)
    }
  }
}
