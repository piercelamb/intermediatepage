package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs.ws._
import play.api.Play.current

case class NewPerson(firstName: String, lastName: String, email: String)
case class Person(id: Long, firstName: String, lastName: String, email: String)

object Person {

  val parser = {
    get[Long]("id") ~
      get[String]("firstname") ~
      get[String]("lastname") ~
      get[String]("email") map {
      case id ~ firstname ~ lastname ~ email => Person(id, firstname, lastname, email)
    }
  }
  def create(firstName: String, lastName: String, email: String): Person = {
    DB.withConnection { implicit c =>
      val id: Long = SQL("INSERT INTO person(firstname, lastname, email) VALUES({firstname}, {lastname}, " +
        "{email})").on('firstname -> firstName, 'lastname -> lastName, 'email -> email)
        .executeInsert(scalar[Long] single)

      return Person.find(id)
    }
  }
  def find(id: Long): Person = {
    DB.withConnection{ implicit c =>
      SQL("SELECT id, firstname, lastname, email FROM person WHERE id = {id}").on('id -> id).using(Person.parser).single()
    }
  }
}