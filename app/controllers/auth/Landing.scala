package controllers.auth

import models.Person
import models.Role._
import models.Page
import models.DataBase
import models.editForm
import models.Account._
import models.Account
import scalikejdbc.ResultName
import views.html
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.mvc._

trait Landing extends Controller with AuthElement_plamb with AuthConfigImpl {

  // The `StackAction` method
  //    takes `(AuthorityKey, Authority)` as the first argument and
  //    a function signature `RequestWithAttributes[AnyContent] => Result` as the second argument and
  //    returns an `Action`

  // thw `loggedIn` method
  //     returns current logged in user

  def demo(name: String) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    val title = "Demo"
    Ok(html.Admin.landing.demo(title, name))
  }


  // Only Administrator can execute this action.
  def choose(name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "Admin"
    Ok(html.Admin.landing.choose(title, name))
  }

  def createUser(name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "Users"
    Ok(html.Admin.landing.create(title, name))
  }

//  val accountForm = Form(
//  mapping(
//  "id" -> ignored(23L),
//  "email" -> email,
//  "password" -> nonEmptyText,
//  "name" -> nonEmptyText,
//  "role" -> nonEmptyText
//  )(Account.apply)(Account.unapply)
//  )

  def db(page: Int, orderBy: Int, title: String, name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "Database"
    val rows: Page[DataBase] = Person.list(page, orderBy)
    Ok(html.Admin.landing.db(title, rows, orderBy, name))
  }

  val personForm = Form(
  mapping(
  "city" -> optional(text),
  "regionName" -> optional(text),
  "country" -> optional(text),
  "firstName" -> optional(text),
  "lastName" -> optional(text),
  "nameRaw" -> optional(text),
  "email" -> email,
  "screenName" -> optional(text),
  "checked" -> boolean
  )(editForm.apply)(editForm.unapply)
  )

  def edit(id: Long, title: String, name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "Edit Person"
    Person.findById(id).map { person =>
      Ok(html.Admin.landing.editForm(id, personForm.fill(person), title, name, person.email))
    }.getOrElse(NotFound)
  }

  def update(id: Long, title: String, name:String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    println("update initiated")
    personForm.bindFromRequest.fold(
    formWithErrors => BadRequest(html.Admin.landing.editForm(id, formWithErrors, title, name,formWithErrors.errors.toString())),
    editForm => {
      println("bindForm success")
      Person.updateDataBase(id, editForm)
      println("DB updated")
      println("\n\nPerson is updated, redirecting\n\n")
      Redirect(controllers.auth.routes.Landing.db(0, 2,title,name)).flashing("success" -> "Person %s has been updated".format(editForm.email))
    }
    )
  }

  def delete(id: Long, title: String, name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
  Person.delete(id)
    Redirect(controllers.auth.routes.Landing.db(0, 2,title,name)).flashing("success" -> "Person has been deleted")
  }

  def analytics(name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "SnappyData Analytics"
    Ok(html.Admin.landing.analytics(title, name))
  }


}

object Landing extends Landing
