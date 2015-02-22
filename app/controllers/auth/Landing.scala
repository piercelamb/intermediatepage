package controllers.auth

import models.Role._
import play.api.mvc._
import views.html

trait Landing extends Controller with Pjax with AuthElement_plamb with AuthConfigImpl {

  // The `StackAction` method
  //    takes `(AuthorityKey, Authority)` as the first argument and
  //    a function signature `RequestWithAttributes[AnyContent] => Result` as the second argument and
  //    returns an `Action`

  // thw `loggedIn` method
  //     returns current logged in user

  def demo = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    val title = "SnappyData demo"
    Ok(html.Admin.landing.demo(title))
  }


  // Only Administrator can execute this action.
  def choose = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "SnappyData Admin"
    Ok(html.Admin.landing.choose(title))
  }

  protected val fullTemplate: User => Template = html.Admin.fullTemplate.apply

}

object Landing extends Landing
