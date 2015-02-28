package controllers.auth

import models._
import models.Role._
import models.Account.addNewAccount
import models.newAccount
import views.html
import play.api.data.{Form}
import play.api.data.Forms._
import play.api.mvc._
import scala.Nothing

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
    Ok(html.Admin.landing.create(accountForm, title, name))
  }

  val accountForm = Form(
  mapping(
  "email" -> email,
  "password" -> nonEmptyText,
  "firstName" -> nonEmptyText,
  "role" -> nonEmptyText.verifying("Must be NormalUser or Administrator", Set("NormalUser", "Administrator").contains(_))
  )(newAccount.apply)(newAccount.unapply)
  )

  def addAccount(title: String, name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    accountForm.bindFromRequest.fold(
    formWithErrors => BadRequest(html.Admin.landing.create(formWithErrors, title, name)),
    newAccount => {
      val id = addNewAccount(newAccount.email, newAccount.password, newAccount.firstName, Role.valueOf(newAccount.role))
      Redirect(controllers.auth.routes.Landing.createUser(name)).flashing("success" -> "Account %s has been created".format(newAccount.firstName))
    }
    )
  }

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
    personForm.bindFromRequest.fold(
    formWithErrors => BadRequest(html.Admin.landing.editForm(id, formWithErrors, title, name,formWithErrors.errors.toString())),
    editForm => {
      Person.updateDataBase(id, editForm)
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
