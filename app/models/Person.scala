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

  def insertLocation(ip: String): String = {
    val url = "http://ip-api.com/json/" + ip
    val result = scala.io.Source.fromURL(url).mkString
    println(result)
    result
  }
}