package Utils


import play.api.{Logger, Play}
import play.api.libs.ws.{WSAuthScheme, WSResponse, WS}
import play.api.Play.current
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object Twitter {

  lazy val bearerToken: Future[String] = {
    require(Play.current.configuration.getString("twitter.consumer.key").isDefined)
    require(Play.current.configuration.getString("twitter.consumer.secret").isDefined)

    WS.url("https://api.twitter.com/oauth2/token")
      .withAuth(Play.current.configuration.getString("twitter.consumer.key").get, Play.current.configuration.getString("twitter.consumer.secret").get, WSAuthScheme.BASIC)
      .post(Map("grant_type" ->  Seq("client_credentials")))
      .withFilter(response => (response.json \ "token_type").asOpt[String] == Some("bearer"))
      .map(response => (response.json \ "access_token").as[String])
  }

  def fetchUsers(bearerToken: String, query: String): Future[WSResponse] = {
    println("this is the bearerToken: " +bearerToken)

    WS.url(query)
      .withHeaders("Authorization" -> s"Bearer $bearerToken")
      .get
  }

}
