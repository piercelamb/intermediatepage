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



class TwitterParse extends Actor {

  //credentials needed for twitter
  val KEY = ConsumerKey(current.configuration.getString("twitter.consumer.key").get, current.configuration.getString("twitter.consumer.secret").get)
  val TOKEN = RequestToken(current.configuration.getString("twitter.access.token").get, current.configuration.getString("twitter.access.secret").get)

  //variables i use throughout
  private var buildURL: String = _
  private val urlBoilerPlate = "https://api.twitter.com/1.1/users/search.json?q="
  private val pageCount = "&page=1&count=10"
  private var score: Int = _

  //JSON reader for twitter
  implicit val twitterReads: Reads[TwitterData] = (
    (JsPath \ "id_str").readNullable[String] and
      (JsPath \ "name").readNullable[String] and
      (JsPath \ "screen_name").readNullable[String] and
      (JsPath \ "followers_count").readNullable[Long] and
      (JsPath \ "location").readNullable[String] and
      (JsPath \ "description").readNullable[String]
    )(TwitterData.apply _)

//Method for comparing twitter location to IP location
  def compareLocation(twitlocation: String, city: Option[String], regionName: Option[String], regionCode: Option[String], country: Option[String], countryCode: Option[String]) = {

    //compare city, regionCode (Portland, OR)
    if (twitlocation.contains(", ")) {
      val cityRegion = city.getOrElse("") + ", " + regionCode.getOrElse("")
      if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
        score += 4
      } //compare city, country (Portland, United States)
      else {
        val cityRegion = city.getOrElse("") + ", " + country.getOrElse("")
        if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
          score += 3
        } //compare city, country (Los Angeles, US)
        else {
          val cityRegion = city.getOrElse("") + ", " + countryCode.getOrElse("")
          if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
            score += 4
          } //compare city, region (Portland, Oregon)
          else {
            val cityRegion = city.getOrElse("") + ", " + regionName.getOrElse("")
            if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
              score += 4
            } //compare city, region, countryCode (Portland, Oregon, US)
            else {
              val cityRegion = city.getOrElse("") + ", " + regionName.getOrElse("") + ", " + countryCode.getOrElse("")
              if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                score += 4
              } //compare city, region, countryCode (Portland, Oregon, United States)
              else {
                val cityRegion = city.getOrElse("") + ", " + regionName.getOrElse("") + ", " + country.getOrElse("")
                if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                  score += 4
                } //compare city, regionCode, countryCode (Portland, OR, US)
                else {
                  val cityRegion = city.getOrElse("") + ", " + regionCode.getOrElse("") + ", " + countryCode.getOrElse("")
                  if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                    score += 4
                  } //compare city, regionCode, countryCode (Portland, OR, United States)
                  else {
                    val cityRegion = city.getOrElse("") + ", " + regionCode.getOrElse("") + ", " + country.getOrElse("")
                    if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                      score += 4
                    } //compare regionCode, country (OR, United States)
                    else {
                      val cityRegion = regionCode.getOrElse("") + ", " + country.getOrElse("")
                      if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                        score += 3
                      } //compare region, country (Oregon, United States)
                      else {
                        val cityRegion = regionName.getOrElse("") + ", " + country.getOrElse("")
                        if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                          score += 3
                        } //compare region, countryCode (Oregon, US)
                        else {
                          val cityRegion = regionName.getOrElse("") + ", " + countryCode.getOrElse("")
                          if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                            score += 3
                          } //compare on regionCode, countryCode (OR, US)
                          else {
                            val cityRegion = regionCode.getOrElse("") + ", " + countryCode.getOrElse("")
                            if (twitlocation.toLowerCase() == cityRegion.toLowerCase()) {
                              score += 3
                            } //compare on ", " complete
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    //twitlocation does not contain ", " therefore probably just "Portland" or "London" or "United States"
    //compare on city vs city
    else {
      if (twitlocation.toLowerCase() == city.getOrElse("").toLowerCase()) {
        score += 3
      } //compare on region vs region
      else {
        if (twitlocation.toLowerCase() == regionName.getOrElse("").toLowerCase()) {
          score += 2
        } //compare on regionCode vs regionCode
        else {
          if (twitlocation.toLowerCase() == regionCode.getOrElse("").toLowerCase()) {
            score += 2
          } //compare on country vs country
          else {
            if (twitlocation.toLowerCase() == country.getOrElse("").toLowerCase()) {
              score += 1
            } //compare on countryCode vs countryCode
            else {
              if (twitlocation.toLowerCase() == countryCode.getOrElse("").toLowerCase()) {
                score += 1
              } //compare done
            }
          }
        }
      }
    }
  }

  //method for finding keywords in twitter description
  def compareDescription(twitDesc: String) = {

    val dict = List("hadoop","spark","data","scala","java","founder","vc","startups","tech","geek","developer", "develop", "code", "coder", "hacker", "hacking", "hack", "programmer", "programming", "engineer", "engineering", "architect", "software", "investor", "venture", "angel", "entrepreneur")
    val descCompare = twitDesc.split("[\\p{P}\\s\\t\\n\\r<>\\d]+")


    println("\n Twitter description is: " +descCompare.mkString(","))

    dict.foreach { element =>
      descCompare.foreach { item =>
        if (element == item.toLowerCase()) {
          score += 3
          println("Interesting word found: " + element + " score: " + score)
        }
      }
    }
  }

  def receive = {

    //case when we aren't able to parse an email for a name
    case RawName(id, name) => {

      println(s"rawName activated. String is: $name")

      //build up the URL to be sent to twitter
      buildURL = urlBoilerPlate + name + pageCount

      println("builtURL: " + buildURL)

      //authenticate and parse twitter response into JSON & validate
      val result = WS.url(buildURL)
        .sign(OAuthCalculator(KEY, TOKEN))
        .get
        .map(result => result.json.validate[List[TwitterData]])

//Await parsed result
      val parseResult = Await.result(result, 3 seconds)

      val twitterdata = parseResult.get


      //get the data we have in the DB to compare
      val DBdata = Person.twitterCompare(id)

      //iterate through each piece of TwitterData
      val dataOut = twitterdata.map( element => {
        score = 0

        //compare rawname to screename
        if (element.screenName.getOrElse("").toLowerCase() == name.toLowerCase()){
          score += 50
        }//compare twitter first name to rawname
        else {

          if (element.name.getOrElse("").contains(" ") && element.name.getOrElse("").dropWhile(_.isSpaceChar).substring(0, element.name.getOrElse("").indexOf(" ")).toLowerCase() == name.toLowerCase()) {
            score += 2
          }
        }
        //compare location
        compareLocation(element.location.getOrElse(""), DBdata.city, DBdata.regionName, DBdata.regionCode, DBdata.country, DBdata.countryCode)

        //find interesting words in description
       compareDescription(element.description.getOrElse(""))

        //map each piece of twitter data to its score
        element -> score

      })
      //find top piece based on score
        val topScore = dataOut.maxBy(_._2)._2

      //assuming it has a score greater than 0, add it to the DB
      if (topScore > 0) {
        val winner: TwitterData = dataOut.maxBy(_._2)._1

        val twitterID = winner.twitterID.getOrElse("")
        val screenName = winner.screenName.getOrElse("")
        val followerCount = winner.followerCount.getOrElse(-1L)

        println("\nAnd the winnder is:\n\n" + winner)

        Person.updateTwitter(id, twitterID, screenName, followerCount)
      }
      else{
        println("\n\n\nNO SCORE GREATER THAN ZERO\n\n\nNAME WAS: "+name)
      }
    }

      //case for when we can parse the name out of the email
    case ParsedName(id, nameArray) => {

      val nmString = nameArray.mkString(" ")

      println("ParsedName activated. String is: " + nmString)

//case for when we have two elements composing the name
      if (nameArray.length == 2) {

        buildURL = urlBoilerPlate + nameArray(0) + "%20" + nameArray(1) + pageCount

      } //case for when we have 3
      else {

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

      //figure out which twitter result creates the highest score
      val dataOut = twitterdata.map( element => {
          score = 0
        //compare first/last vs first/last
        if (element.name.getOrElse("").contains(" ")) {
          val nameCompare = DBdata.firstName.getOrElse("") + " " + DBdata.lastName.getOrElse("")
          if (element.name.getOrElse("").toLowerCase() == nameCompare.toLowerCase()) {
            score += 8
            println("Exact name match, score: " + score)
            //compare full name vs full name
          }

          else {
            val nameCompare = DBdata.firstName.getOrElse("") + " " + DBdata.middleName.getOrElse("") + " " + DBdata.lastName.getOrElse("")
            if (element.name.getOrElse("").toLowerCase() == nameCompare.toLowerCase()) {
              score += 8
              println("Exact full name match, score: " + score)
              //maybe last 2 are hyphenated
            } else {
              val nameCompare = DBdata.firstName.getOrElse("") + " " + DBdata.middleName.getOrElse("") + "-" + DBdata.lastName.getOrElse("")
              if (element.name.getOrElse("").toLowerCase() == nameCompare.toLowerCase()) {
                score += 8
                println("Exact full hyphenated name match, score: " + score)
              }
              //compare just first name (maybe last name is wrong)
              else {
                val nameCompare = DBdata.firstName.getOrElse("")

                if (element.name.getOrElse("").dropWhile(_.isSpaceChar).substring(0, element.name.getOrElse("").indexOf(" ")).toLowerCase() == nameCompare.toLowerCase()) {

                  score += 2
                  println("First name match but last name doesn't")
                }
              }
            }
          }
        }
        //full name doesnt match, compare just the first name
        if (score == 0) {
          val nameCompare = DBdata.firstName.getOrElse("")
          if (element.name.getOrElse("").toLowerCase() == nameCompare.toLowerCase()) {
            score += 2
            println("First name match only, score +2")
          }
        }

        //compare location
        compareLocation(element.location.getOrElse(""), DBdata.city, DBdata.regionName, DBdata.regionCode, DBdata.country, DBdata.countryCode)

        //find interesting words in description
        compareDescription(element.description.getOrElse(""))

        element -> score
      })
println("\n ScoreMap after computations \n")
      dataOut.foreach(println)

      val topScore = dataOut.maxBy(_._2)._2
      if (topScore > 0) {
        val winner: TwitterData = dataOut.maxBy(_._2)._1

        val twitterID = winner.twitterID.getOrElse("")
        val screenName = winner.screenName.getOrElse("")
        val followerCount = winner.followerCount.getOrElse(-1L)

        println("\nAnd the winnder is:\n\n" + winner)

        Person.updateTwitter(id, twitterID, screenName, followerCount)
      }
      else{
        println("\n\n\nNO SCORE GREATER THAN ZERO\n\n\nNAME WAS: "+nameArray.mkString(","))

      }
    }

  }


}
