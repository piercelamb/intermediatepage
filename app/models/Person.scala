package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.ws._
import play.api.Play.current
import play.api.libs.json.{JsValue, Json}
import config.Global._

case class NewPerson(email: String)
case class Person(id: Long, ip: String, email: String)
case class Location(city: String, regionName: String, country: String)

object Person {

  val parser = {
    get[Long]("id") ~
    get[String]("ip") ~
      get[String]("email") map {
      case id ~ ip ~ email => Person(id, ip, email)
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
      SQL("SELECT id, ip, email FROM person WHERE id = {id}").on('id -> id).using(Person.parser).single()
    }
  }

  def insertLocation(id: Long, location: Location) = {
    val city = location.city
    val regionName = location.regionName
    val country = location.country
    DB.withConnection { implicit c =>
     val result: Int = SQL("UPDATE person SET city = {city}, regionname = {regionName}, country = {country} WHERE id = {id}")
       .on('id -> id, 'city -> city, 'regionName -> regionName, 'country -> country)
      .executeUpdate()
    }
  }


  def updateParsed(id: Long, nameRaw: Array[String], companyRaw: String) = {

    println("id is " +id+ " companyRaw is "+companyRaw+ " nameRaw is "+ nameRaw.mkString(" "))

    if (nameRaw.last == null) {
      DB.withConnection { implicit c =>
        val result = SQL("UPDATE person SET nameraw = {nameRaw}, companyraw = {companyRaw} WHERE id = {id}")
          .on('id -> id, 'nameRaw -> nameRaw(0), 'companyRaw -> companyRaw)
          .executeUpdate()
      }
    }
    else if (nameRaw.length == 2 && nameRaw.last != null) {
      DB.withConnection { implicit c =>
        val result = SQL("UPDATE person SET firstname = {FirstName}, lastname = {LastName}, companyraw = {companyRaw} WHERE id = {id}")
          .on('id -> id, 'FirstName -> nameRaw(0), 'LastName -> nameRaw(1), 'companyRaw -> companyRaw)
          .executeUpdate()
      }
    }
    else if (nameRaw.length == 3) {
        DB.withConnection { implicit c =>
          val result = SQL("UPDATE person SET firstname = {FirstName}, middlename = {MiddleName}, lastname = {LastName}, companyraw = {companyRaw} WHERE id = {id}")
            .on('id -> id, 'FirstName -> nameRaw(0), 'MiddleName -> nameRaw(1), 'LastName -> nameRaw(2), 'companyRaw -> companyRaw)
            .executeUpdate()
        }
    }
    else {
      DB.withConnection { implicit c =>
        val result = SQL("UPDATE person SET nameraw = {nameRaw}, companyraw = {companyRaw} WHERE id = {id}")
          .on('id -> id, 'nameRaw -> nameRaw.mkString(""), 'companyRaw -> companyRaw)
          .executeUpdate()
      }

    }
  }

}