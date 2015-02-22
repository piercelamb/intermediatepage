package controllers

/**
 * Created by plamb on 2/21/15.
 */
import play.api.mvc.Result

package object auth {

  type AuthenticityToken = String

  type CookieUpdater = Result => Result
}
