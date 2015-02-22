package controllers.auth

import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


trait LoginLogout_plamb extends CookieSupport_plamb {
  self: Controller with AuthConfigImpl =>

  def gotoLoginSucceeded(userId: Id, userRole: Authority, name: String)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
    gotoLoginSucceeded(userId, loginSucceeded(request, userRole, name))
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
