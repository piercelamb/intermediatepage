package controllers.auth

import models.Person
import models.Role._
import models.Page
import models.DataBase
import play.api.mvc._
import views.html

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

  def db(page: Int, orderBy: Int, title: String, name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "Database"
    val rows: Page[DataBase] = Person.list(page, orderBy)
    Ok(html.Admin.landing.db(title, rows, orderBy, name))
  }



  def analytics(name: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "SnappyData Analytics"
    Ok(html.Admin.landing.analytics(title, name))
  }


}

object Landing extends Landing
