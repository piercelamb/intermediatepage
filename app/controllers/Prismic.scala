package controllers

import play.api.mvc._
import views.html

import scala.concurrent._
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

import io.prismic._

object PrismicMain extends Controller with PrismicController {

  import PrismicHelper._

  // -- Resolve links to documents
  def linkResolver(api: Api)(implicit request: RequestHeader) = DocumentLinkResolver(api) {
    case (docLink, maybeBookmarked)
      if !docLink.isBroken => routes.PrismicMain.detail(docLink.uid.getOrElse(sys.error("no uid found"))).absoluteURL()
    case _ =>
      routes.PrismicMain.brokenLink().absoluteURL()
  }

  // -- Page not found
  def PageNotFound(implicit ctx: PrismicHelper.Context) = NotFound(html.blog.pageNotFound())

  def brokenLink = PrismicAction { implicit request =>
    Future.successful(PageNotFound)
  }

  // -- Home page
  def index(page: Int) = PrismicAction { implicit request =>
    ctx.api.forms("everything").ref(ctx.ref).pageSize(10).page(page).submit() map { response =>
      Ok(html.blog.index(response))
    }
  }

  // -- Document detail
  def detail(uid: String) = PrismicAction { implicit request =>
    for {
      maybeDocument <- getDocument(uid)
    } yield {
      checkUID(maybeDocument, uid) {
        //case Left(uid)   =>  MovedPermanently(routes.PrismicMain.detail(uid).url)
        case Right(document) => Ok(html.blog.detail(document))
      }
    }
  }

  // -- Basic Search
  def search(q: Option[String], page: Int) = PrismicAction { implicit request =>
    ctx.api.forms("everything")
      .query(Predicate.fulltext("document", q.getOrElse("")))
      .ref(ctx.ref).pageSize(10).page(page).submit() map { response =>
      Ok(html.blog.search(q, response))
    }
  }

  // -- Preview Action
  def preview(token: String) = PrismicAction { implicit req =>
    ctx.api.previewSession(token, ctx.linkResolver, routes.PrismicMain.index().url).map { redirectUrl =>
      Redirect(redirectUrl).withCookies(Cookie(Prismic.previewCookie, token, path = "/", maxAge = Some(30 * 60 * 1000), httpOnly = false))
    }
  }

}
