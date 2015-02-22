package controllers.auth

import jp.t2v.lab.play2.stackc.{RequestAttributeKey, RequestWithAttributes, StackableController}
import play.api.mvc.{Controller, Result}
import scala.concurrent.Future


trait AuthElement_plamb extends StackableController with AsyncAuth_plamb {
  self: Controller with AuthConfigImpl =>

  private[controllers] case object AuthKey extends RequestAttributeKey[User]
  case object AuthorityKey extends RequestAttributeKey[Authority]

  override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] = {
    implicit val (r, ctx) = (req, StackActionExecutionContext(req))
    req.get(AuthorityKey) map { authority =>
      authorized(authority) flatMap {
        case Right((user, cookieUpdater)) => super.proceed(req.set(AuthKey, user))(f).map(cookieUpdater)
        case Left(result)                 => Future.successful(result)
      }
    } getOrElse {
      authorizationFailed(req)
    }
  }

  implicit def loggedIn(implicit req: RequestWithAttributes[_]): User = req.get(AuthKey).get

}

trait OptionalAuthElement extends StackableController with AsyncAuth_plamb {
  self: Controller with AuthConfigImpl =>

  private[auth] case object AuthKey extends RequestAttributeKey[User]

  override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] = {
    implicit val (r, ctx) = (req, StackActionExecutionContext(req))
    val maybeUserFuture = restoreUser.recover { case _ => None -> identity[Result] _ }
    maybeUserFuture.flatMap { case (maybeUser, cookieUpdater) =>
      super.proceed(maybeUser.map(u => req.set(AuthKey, u)).getOrElse(req))(f).map(cookieUpdater)
    }
  }

  implicit def loggedIn[A](implicit req: RequestWithAttributes[A]): Option[User] = req.get(AuthKey)
}

trait AuthenticationElement extends StackableController with AsyncAuth_plamb {
  self: Controller with AuthConfigImpl =>

  private[auth] case object AuthKey extends RequestAttributeKey[User]

  override def proceed[A](req: RequestWithAttributes[A])(f: RequestWithAttributes[A] => Future[Result]): Future[Result] = {
    implicit val (r, ctx) = (req, StackActionExecutionContext(req))
    restoreUser recover {
      case _ => None -> identity[Result] _
    } flatMap {
      case (Some(u), cookieUpdater) => super.proceed(req.set(AuthKey, u))(f).map(cookieUpdater)
      case (None, _)                => authenticationFailed(req)
    }
  }

  implicit def loggedIn(implicit req: RequestWithAttributes[_]): User = req.get(AuthKey).get

}
