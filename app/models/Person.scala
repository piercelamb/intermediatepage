package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.ws._
import play.api.Play.current

case class NewPerson(email: String)
case class Person(id: Long, email: String)

object Person {

  val parser = {
    get[Long]("id") ~
      get[String]("email") map {
      case id ~ email => Person(id, email)
    }
  }
  def create(email: String): Person = {
    DB.withConnection { implicit c =>
      val id: Long = SQL("INSERT INTO person(email) VALUES({email})").on('email -> email)
        .executeInsert(scalar[Long] single)

      return Person.find(id)
    }
  }
  def find(id: Long): Person = {
    DB.withConnection{ implicit c =>
      SQL("SELECT id, email FROM person WHERE id = {id}").on('id -> id).using(Person.parser).single()
    }
  }
}