package services
import play.api.i18n._
import play.api.libs.json._
import play.api.libs.ws._
import scala.concurrent.Future
import play.api.Play.current
/**
 * Created by plamb on 6/12/15.
 */
object Messages {
  def buildRegistrationTemplate(email: String, mandrillKey: String): JsValue = {
    JsObject(Seq(
      "key" -> JsString(mandrillKey),
      "template_name" -> JsString("snappybeta"),
      "template_content" -> JsArray(Seq(
      JsObject(Seq("name" -> JsString(""), "content" -> JsString("")))
      )),
        //JsObject(Seq())))),
       //JsObject(Seq("name" -> JsString("firstname"), "content" -> JsString(email)))
       // JsObject(Seq("name" -> JsString("appname"), "content" -> JsString("Snappy"))))),
      "message" -> JsObject(
        Seq("to" -> JsArray(Seq(
          JsObject(Seq("email" -> JsString(email),
//            "name" -> JsString(firstName + " " + lastName),
            "type" -> JsString("to")
          )))),
          "headers" -> JsObject(Seq(
            "Reply-To" -> JsString("chomp@snappydata.io"))),
          "important" -> JsBoolean(false),
          "track_opens" -> JsBoolean(true)))))
  }

  def emailNewRegistration(email: String) {
    val mandrillKey: String = play.Play.application.configuration.getString("mandrillKey")
    val jsonClass = buildRegistrationTemplate(email, mandrillKey)
    println("data sent to mandrill is: " +jsonClass)
    val apiUrl = "https://mandrillapp.com/api/1.0/messages/send-template.json"
    val futureResponse: Future[WSResponse] = WS.url(apiUrl).post(jsonClass)
  }
}
