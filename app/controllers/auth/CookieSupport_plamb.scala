package controllers.auth

import play.api.libs.Crypto
import play.api.mvc.{Cookie, Result}

trait CookieSupport_plamb { self: AuthConfigImpl =>

  def verifyHmac(cookie: Cookie): Option[String] = {
    val (hmac, value) = cookie.value.splitAt(40)
    if (Crypto.sign(value) == hmac) Some(value) else None
  }

  def bakeCookie(token: String)(result: Result): Result = {
    val value = Crypto.sign(token) + token
    val maxAge = if (isTransientCookie) None else Some(sessionTimeoutInSeconds)
    result.withCookies(Cookie(cookieName, value, maxAge, cookiePathOption, cookieDomainOption, cookieSecureOption, cookieHttpOnlyOption))
  }

}