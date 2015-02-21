package controllers

import play.api.mvc._
import play.api.mvc.Cookie
import play.api.libs.Crypto
import jp.t2v.lab.play2.auth.CookieSupport
import scala.concurrent.{Future, ExecutionContext}


trait LoginLogout_plamb extends CookieSupport {
  self: Controller with AuthConfigImpl =>

  def gotoLoginSucceeded(userId: Id, userRole: Authority)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    gotoLoginSucceeded(userId, loginSucceeded(request, userRole))
  }

  def gotoLoginSucceeded(userId: Id, result: => Future[Result])(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = for {
    token <- idContainer.startNewSession(userId, sessionTimeoutInSeconds)
    r     <- result
  } yield bakeCookie(token)(r)

  def gotoLogoutSucceeded(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    gotoLogoutSucceeded(logoutSucceeded(request))
  }

  def gotoLogoutSucceeded(result: => Future[Result])(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    request.cookies.get(cookieName) flatMap verifyHmac foreach idContainer.remove
    result.map(_.discardingCookies(DiscardingCookie(cookieName)))
  }
}
