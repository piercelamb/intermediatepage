package controllers

import jp.t2v.lab.play2.auth.AuthElement
import play.api.mvc._
import models.Role._
import views.html

trait Landing extends Controller with Pjax with AuthElement with AuthConfigImpl {

  // The `StackAction` method
  //    takes `(AuthorityKey, Authority)` as the first argument and
  //    a function signature `RequestWithAttributes[AnyContent] => Result` as the second argument and
  //    returns an `Action`

  // thw `loggedIn` method
  //     returns current logged in user

  def main = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    val title = "message main"
    Ok(html.Admin.landing.main(title))
  }


  // Only Administrator can execute this action.
  def write = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "write message"
    Ok(html.Admin.landing.write(title))
  }

  protected val fullTemplate: User => Template = html.Admin.fullTemplate.apply

}

object Landing extends Landing
