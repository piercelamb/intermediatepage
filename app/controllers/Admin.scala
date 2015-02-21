package controllers

import play.api.data.Form
import play.api.mvc._
import play.api.data.Forms._
import views.html
import models.Account
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Admin extends Controller with LoginLogout_plamb with AuthConfigImpl {

  /** Your application's login form.  Alter it to fit your application */
  val loginForm = Form {
    mapping("email" -> email, "password" -> text)(Account.authenticate)(_.map(u => (u.email, "")))
      .verifying("Invalid email or password", result => result.isDefined)
  }

  /** Alter the login page action to suit your application. */
  def login = Action { implicit request =>
    Ok(html.Admin.login(loginForm))
  }

  /**
   * Return the `gotoLogoutSucceeded` method's result in the logout action.
   *
   * Since the `gotoLogoutSucceeded` returns `Future[Result]`,
   * you can add a procedure like the following.
   *
   *   gotoLogoutSucceeded.map(_.flashing(
   *     "success" -> "You've been logged out"
   *   ))
   */
  def logout = Action.async { implicit request =>
    gotoLogoutSucceeded
  }

  /**
   * Return the `gotoLoginSucceeded` method's result in the login action.
   *
   * Since the `gotoLoginSucceeded` returns `Future[Result]`,
   * you can add a procedure like the `gotoLogoutSucceeded`.
   */
  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.Admin.login(formWithErrors))),
      user => {
        val lol = user.get.role
        println("user role is: "+lol)
        gotoLoginSucceeded(user.get.id, user.get.role)
      }
    )
  }

}
