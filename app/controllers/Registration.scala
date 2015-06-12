package controllers
import play.api._
import play.api.mvc._
import play.api.i18n
import services.Messages
import models.{Person, NewPerson, FindLoc, FindName}
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import akka.actor.Props
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import Actors.{GeoLookup, NameParse}
import config.Global._

object Registration extends Controller {
//Makes the geoLookup actor available using the default Play context
  val geoLookup = Akka.system.actorOf(Props[GeoLookup], name = "geoLookup")
  val nameParse = Akka.system.actorOf(Props[NameParse], name = "nameParse")

//Mapping for the email me form
  val newPersonForm: Form[NewPerson] = Form(
    mapping(
      "email" -> nonEmptyText(minLength = 6) //shortest domain is k.st add @ + 1 letter and the min email length is 6
    )(NewPerson.apply)(NewPerson.unapply)
  )

  //Will return a new registered Person model to populate
  def newReg = Action {
    Ok(views.html.registration.newReg(newPersonForm))
  }

  var newPerson: Person = _
//Once the email me form is created, this code creates a new Person object in the database then sends
//the new IP and ID to the geoLookup actor indicating there is a new IP to do a lookup on and insert in the DB
  //Will create a new registered Person model
  def create = Action { implicit request =>
    newPersonForm.bindFromRequest.fold(
      errors => BadRequest(views.html.registration.newReg(errors)),
      person => {
        newPerson = Person.create(ip, person.email)
        Messages.emailNewRegistration(person.email)
      }
    )
  //send messages off to actors
  geoLookup ! FindLoc(newPerson.id, newPerson.ip)
  nameParse ! FindName(newPerson.id, newPerson.email)
    //Ok(views.html.registration.newReg(newPersonForm))
  Ok(views.html.registration.registrationSuccess(newPerson.email))
  }

}

