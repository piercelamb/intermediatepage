package controllers.auth


import jp.t2v.lab.play2.auth.{AsyncIdContainer, CacheIdContainer}
import models.Role._
import models.{Account, Role}
import play.api.mvc.Results._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._
import config.Global._

trait AuthConfigImpl {

  /**
   * A type that is used to identify a user.
   * `String`, `Int`, `Long` and so on.
   */
  type Id = Int

  /**
   * A type that represents a user in your application.
   * `User`, `Account` and so on.
   */
  type User = Account

  /**
   * A type that is defined by every action for authorization.
   * This sample uses the following trait:
   *
   * sealed trait Role
   * case object Administrator extends Role
   * case object NormalUser extends Role
   */
  type Authority = Role

  /**
   * A `ClassTag` is used to retrieve an id from the Cache API.
   * Use something like this:
   */
  val idTag: ClassTag[Id] = classTag[Id]

  /**
   * The session timeout in seconds
   */
  val sessionTimeoutInSeconds: Int = 3600

  /**
   * A function that returns a `User` object from an `Id`.
   * You can alter the procedure to suit your application.
   */
  def resolveUser(id: Id)(implicit ctx: ExecutionContext) = Future.successful(Account.findById(id))

  /**
   * Where to redirect the user after a successful login.
   */
  def loginSucceeded(request: RequestHeader, userRole: Authority, userName: String)(implicit ctx: ExecutionContext): Future[Result] = {

    if (userRole == Role.Administrator) {
      println("User "+userName+ " with IP "+ip+" logged into admin/landing with " +userRole)

      Future.successful(Redirect(routes.Landing.choose(userName)))
    }
    else {

      println("User "+userName+ " with IP "+ip+" logged into demo with " +userRole)
      Future.successful(Redirect(routes.Landing.demo(userName)))
    }
  }
  /**
   * Where to redirect the user after logging out
   */
  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(controllers.routes.Admin.login))

  /**
   * If the user is not logged in and tries to access a protected resource then redirct them as follows:
   */
  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Redirect(controllers.routes.Admin.login))

  /**
   * If authorization failed (usually incorrect password) redirect the user as follows:
   */
  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext): Future[Result] =
    Future.successful(Forbidden("no permission"))

  /**
   * A function that determines what `Authority` a user has.
   * You should alter this procedure to suit your application.
   */
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext): Future[Boolean] = Future.successful {
    (user.role, authority) match {
      case (Administrator, _)       => true
      case (NormalUser, NormalUser) => true
      case _                        => false
    }
  }


  lazy val idContainer: AsyncIdContainer[Id] = AsyncIdContainer(new CacheIdContainer[Id])

  lazy val cookieName: String = "PLAY2AUTH_SESS_ID"

  lazy val cookieSecureOption: Boolean = false

  lazy val cookieHttpOnlyOption: Boolean = true

  lazy val cookieDomainOption: Option[String] = None

  lazy val cookiePathOption: String = "/"

  lazy val isTransientCookie: Boolean = false
  /**
   * Whether use the secure option or not use it in the cookie.
   * However default is false, I strongly recommend using true in a production.
   */
  //override lazy val cookieSecureOption: Boolean = play.api.Play.isProd(play.api.Play.current)

  /**
   * Whether a login session is closed when the brower is terminated.
   * default is false.
   */
  //override lazy val isTransientCookie: Boolean = false

}
