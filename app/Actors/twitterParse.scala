package Actors

import play.api.Play.current
import play.api.libs.ws.WS
import akka.actor.Actor
import play.api.Logger
import Utils.Twitter
import scala.concurrent.ExecutionContext.Implicits.global
import models.{RawName, ParsedName}



class TwitterParse extends Actor {


  private var buildURL: String = _
  private val urlBoilerPlate = "https://api.twitter.com/1.1/users/search.json?q="
  private val pageCount = "&page=1&count=3"


  def receive = {

    case RawName(id, name) => {

      println(s"rawName activated. String is: $name")

      buildURL = urlBoilerPlate + name + pageCount

      println("builtURL: " + buildURL)

      try {
        Twitter.bearerToken.flatMap { bearerToken =>
          Twitter.fetchUsers(bearerToken, buildURL).map { response =>
            println(response.json)
          }
        }
      } catch {
        case illegalArgumentException: IllegalArgumentException =>
          Logger.error("Twitter Bearer Token is missing", illegalArgumentException)
      }
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

      try {
        Twitter.bearerToken.flatMap { bearerToken =>
          Twitter.fetchUsers(bearerToken, buildURL).map { response =>
            println(response.json)
          }
        }
      } catch {
        case illegalArgumentException: IllegalArgumentException =>
          Logger.error("Twitter Bearer Token is missing", illegalArgumentException)
      }

    }

  }

}
