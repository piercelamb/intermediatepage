package models

import Actors.TwitterParse
import akka.actor.Props
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.ws._
import play.api.Play.current
import play.api.libs.json.{JsValue, Json}
import config.Global._

case class NewPerson(email: String)
case class Person(id: Long, ip: String, email: String)
case class PersonForTwitter(id: Long, city: Option[String], regionName: Option[String], regionCode: Option[String], country: Option[String], countryCode: Option[String], firstName: Option[String], middleName: Option[String], lastName: Option[String], nameRaw: Option[String])

case class FindLoc(id: Long, ip: String)
case class Location(city: String, regionName: String, regionCode: String, country: String, countryCode: String)

case class FindName(id: Long, email: String)

case class RawName(id: Long, name: String)
case class ParsedName (id: Long, nameArray: Array[String])
case class TwitterData(twitterID: Option[String], name: Option[String], screenName: Option[String], followerCount: Option[Long], location: Option[String], description:Option[String])

object Person {

  val parserShort = {
    get[Long]("id") ~
    get[String]("ip") ~
      get[String]("email") map {
      case id ~ ip ~ email => Person(id, ip, email)
    }
  }

  val parserLong = {
    get[Long]("id") ~
      get[Option[String]]("city") ~
    get[Option[String]]("regionName") ~
    get[Option[String]]("regionCode") ~
    get[Option[String]]("country") ~
    get[Option[String]]("countryCode") ~
    get[Option[String]]("firstName") ~
    get[Option[String]]("middleName") ~
    get[Option[String]]("lastName") ~
      get[Option[String]]("nameRaw") map {

      case id ~ city ~ regionName ~ regionCode ~ country ~ countryCode ~ firstName ~ middleName ~ lastName ~ nameRaw =>
        PersonForTwitter(id, city, regionName, regionCode, country, countryCode, firstName, middleName, lastName, nameRaw)
    }
  }

  def create(ip: String, email: String): Person = {
    DB.withConnection { implicit c =>
      val id: Long = SQL("INSERT INTO person(ip, email) VALUES({ip}, {email})").on('ip -> ip, 'email -> email)
        .executeInsert(scalar[Long] single)

      return Person.find(id)
    }
  }
  def find(id: Long): Person = {
    DB.withConnection{ implicit c =>
      SQL("SELECT id, ip, email FROM person WHERE id = {id}").on('id -> id).using(Person.parserShort).single()
    }
  }

  def twitterCompare(id: Long): PersonForTwitter = {
    DB.withConnection{ implicit c =>
      SQL("SELECT id, city, regionname, regioncode, country, countrycode, firstname, middlename, lastname, nameraw FROM person WHERE id = {id}").on('id -> id).using(Person.parserLong).single()
    }
  }

  def insertLocation(id: Long, location: Location) = {
    val city = location.city
    val regionName = location.regionName
    val regionCode = location.regionCode
    val country = location.country
    val countryCode = location.countryCode
    DB.withConnection { implicit c =>
     val result: Int = SQL("UPDATE person SET city = {city}, regionname = {regionName}, regioncode = {regionCode}, country = {country}, countrycode = {countryCode} WHERE id = {id}")
       .on('id -> id, 'city -> city, 'regionName -> regionName, 'regionCode -> regionCode, 'country -> country, 'countryCode -> countryCode)
      .executeUpdate()
    }
  }

  val twitterParse = Akka.system.actorOf(Props[TwitterParse], name = "twitterParse")

  def updateParsed(id: Long, nameRaw: Array[String], companyRaw: String) = {

    println("id is " +id+ " companyRaw is "+companyRaw+ " nameRaw is "+ nameRaw.mkString(" "))

    if (nameRaw.last == null) {

      val name: String = nameRaw(0)
      println("name added to DB in raw form "+name)
      DB.withConnection { implicit c =>
        val result = SQL("UPDATE person SET nameraw = {nameRaw}, companyraw = {companyRaw} WHERE id = {id}")
          .on('id -> id, 'nameRaw -> name, 'companyRaw -> companyRaw)
          .executeUpdate()
      }
      twitterParse ! RawName(id, name)
    }
    else if (nameRaw.length == 2 && nameRaw.last != null) {
      println("name added to DB with first & last "+ nameRaw.mkString(" "))
      DB.withConnection { implicit c =>
        val result = SQL("UPDATE person SET firstname = {FirstName}, lastname = {LastName}, companyraw = {companyRaw} WHERE id = {id}")
          .on('id -> id, 'FirstName -> nameRaw(0), 'LastName -> nameRaw(1), 'companyRaw -> companyRaw)
          .executeUpdate()
      }
      println("sending package to twitterParse")
      twitterParse ! ParsedName(id, nameRaw)
    }
    else if (nameRaw.length == 3) {
        DB.withConnection { implicit c =>
          val result = SQL("UPDATE person SET firstname = {FirstName}, middlename = {MiddleName}, lastname = {LastName}, companyraw = {companyRaw} WHERE id = {id}")
            .on('id -> id, 'FirstName -> nameRaw(0), 'MiddleName -> nameRaw(1), 'LastName -> nameRaw(2), 'companyRaw -> companyRaw)
            .executeUpdate()
        }
      twitterParse ! ParsedName(id, nameRaw)
    }
    else {
      DB.withConnection { implicit c =>
        val result = SQL("UPDATE person SET nameraw = {nameRaw}, companyraw = {companyRaw} WHERE id = {id}")
          .on('id -> id, 'nameRaw -> nameRaw.mkString(""), 'companyRaw -> companyRaw)
          .executeUpdate()
      }

    }
  }

  def updateTwitter(id: Long, twitterID: String, screenName: String, followerCount: Long) = {

    println("\nData received by DB\n\n" +id+", "+twitterID+", "+screenName+", "+followerCount)
    DB.withConnection { implicit c =>
      val result = SQL("UPDATE person SET twitterid = {twitterID}, screenname = {screenName}, followercount = {followerCount} WHERE id = {id}")
        .on('id -> id, 'twitterID -> twitterID, 'screenName -> screenName, 'followerCount -> followerCount)
        .executeUpdate()
    }

  }

}