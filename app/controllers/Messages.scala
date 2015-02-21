package controllers

import jp.t2v.lab.play2.auth.AuthElement
import play.api.mvc._
import models.Role._
import views.html

object Messages extends Controller with AuthElement with AuthConfigImpl {

  // The `StackAction` method
  //    takes `(AuthorityKey, Authority)` as the first argument and
  //    a function signature `RequestWithAttributes[AnyContent] => Result` as the second argument and
  //    returns an `Action`

  // thw `loggedIn` method
  //     returns current logged in user

  def main = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    val title = "message main"
    Ok(html.Admin.message.main(title))
  }

  def list = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    val title = "all messages"
    Ok(html.Admin.message.list(title))
  }

  def detail(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val user = loggedIn
    val title = "messages detail "
    Ok(html.Admin.message.detail(title + id))
  }

  // Only Administrator can execute this action.
  def write = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val user = loggedIn
    val title = "write message"
    Ok(html.Admin.message.write(title))
  }

}
