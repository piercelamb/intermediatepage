package Actors

import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json.{JsResult, Json, JsPath, Reads}
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.ws.WS
import akka.actor.{Props, Actor}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import models.{Person, RawName, ParsedName, TwitterData}
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.RequestToken

//case class TwitterData(twitterID: Long, name: String, screenName: String, location: String, description: String)

class TwitterParse extends Actor {

  val KEY = ConsumerKey(current.configuration.getString("twitter.consumer.key").get, current.configuration.getString("twitter.consumer.secret").get)
  val TOKEN = RequestToken(current.configuration.getString("twitter.access.token").get, current.configuration.getString("twitter.access.secret").get)

  private var buildURL: String = _
  private val urlBoilerPlate = "https://api.twitter.com/1.1/users/search.json?q="
  private val pageCount = "&page=1&count=5"
  private var score: Int = _

  implicit val twitterReads: Reads[TwitterData] = (
    (JsPath \ "id_str").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "screen_name").read[String] and
      (JsPath \ "location").read[String] and
      (JsPath \ "description").read[String]
    )(TwitterData.apply _)


  def receive = {

    case RawName(id, name) => {

      println(s"rawName activated. String is: $name")

      buildURL = urlBoilerPlate + name + pageCount

      println("builtURL: " + buildURL)

      val result = WS.url(buildURL)
        .sign(OAuthCalculator(KEY, TOKEN))
        .get
        .map(result => result.json.validate[List[TwitterData]])


      val parseResult = Await.result(result, 3 seconds)

      val twitterdata = parseResult.get

      twitterdata.foreach(println)


    }

    case ParsedName(id, nameArray) => {

      val nmString = nameArray.mkString(" ")

      println("ParsedName activated. String is: " + nmString)


      if (nameArray.length == 2) {


        buildURL = urlBoilerPlate + nameArray(0) + "%20" + nameArray(1) + pageCount

      } else {

        buildURL = urlBoilerPlate + nameArray(0) + "%20" + nameArray(1) + "%20" + nameArray(2) + pageCount

      }

      println("builtURL: " + buildURL)

       val result = WS.url(buildURL)
                    .sign(OAuthCalculator(KEY, TOKEN))
                    .get
                    .map(result => result.json.validate[List[TwitterData]])

      val parseResult = Await.result(result, 3 seconds)
      val twitterdata = parseResult.get
      val DBdata = Person.twitterCompare(id)
      println("Twitter data is: " + twitterdata)
      println("DB data is: " +DBdata)

      //create a score list to convert twitterdata to a Map[Score -> twitterdata]
//      val initializeScores = List(0,0,0,0,0)
//      val scoreMap = (twitterdata zip initializeScores).toMap



      //figure out which twitter result creates the highest score
      val dataOut = twitterdata.map( element => {
          score = 0
        //compare first/last vs first/last
        if (element.name.contains(" ")) {
          val nameCompare = DBdata.firstName.getOrElse("") + " " + DBdata.lastName.getOrElse("")
          if (element.name.toLowerCase() == nameCompare.toLowerCase()) {
            score += 5
            println("Exact name match, score: " +score)
            //compare full name vs full name
          }
        }
        else {
          val nameCompare = DBdata.firstName.getOrElse("") + " " + DBdata.middleName.getOrElse("") + " " + DBdata.lastName.getOrElse("")
          if (element.name.toLowerCase() == nameCompare.toLowerCase()) {
            score += 5
            println("Exact full name match, score: " +score)
            //compare just first name (maybe last name is wrong)
          } else {
            val nameCompare = DBdata.firstName.getOrElse("")
            if (element.name.substring(0, element.name.indexOf(" ")).toLowerCase() == nameCompare.toLowerCase()) {
              score += 2
              println("First name match but last name doesn't")
            }
          }
        }

        if (score == 0) {
          val nameCompare = DBdata.firstName.getOrElse("")
          if (element.name.toLowerCase() == nameCompare.toLowerCase()) {
            score += 2
            println("First name match only, score +2")
          }
        }

        element -> score

//        scoreMap(element).updated(element._1, score)
        //println("\n "+score+" score after computation \n")



      })
println("\n ScoreMap after computations \n")
      dataOut.foreach(println)

    }

  }

//  val twitterDecide = Akka.system.actorOf(Props[TwitterDecide], name = "twitterDecide")
//
//  twitterDecide ! (RawName., twitterdata)

}
