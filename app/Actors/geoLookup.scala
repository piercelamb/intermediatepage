package Actors

import akka.actor.Actor
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{Person, Location}

//this actor takes the passed ID and IP of the Person just created and pings a geolocation API.
//it parses the returned JSON and updates the passed ID in the DB with the location data

class GeoLookup extends Actor {

  //JSON reader for the geolocation API

  implicit val locationReads: Reads[Location] = (
    (JsPath \ "city").read[String] and
      (JsPath \ "regionName").read[String] and
      (JsPath \ "country").read[String]
    )(Location.apply _)


  def receive = {
    //Actor is passed ID, IP
    case ip: (Long, String) =>

      println("IP recevied by GeoLookup " + ip._2)
      val url = "http://ip-api.com/json/" + "24.22.84.77"
      //create URL and hit the API
      val result = scala.io.Source.fromURL(url).mkString
      //Turn result into string and parse
      val jsonResult = Json.parse(result)
      //validate the JSON using the implicit parser
      val parseResult: JsResult[Location] = jsonResult.validate[Location]
      //get the result as a Location
      val location = parseResult.get
      //Update location data using the ID
      Person.insertLocation(ip._1, location)

  }
}
