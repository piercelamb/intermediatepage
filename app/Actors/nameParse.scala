package Actors

import akka.actor.Actor
import models.Person
import models.Person.{updateParsed}


//this actor takes the passed ID and IP of the Person just created and pings a geolocation API.
//it parses the returned JSON and updates the passed ID in the DB with the location data

class NameParse extends Actor {

  private var names: Array[String] = _
  private var companyRaw: String = _


  def receive = {
    case email: (Long, String) =>

      println("Email received by nameParse: " + email._2 )

      val numToRemove = "0123456789".toSet
      val splitDomain = email._2.split('@')
      val shedDomain = splitDomain(0).filterNot(numToRemove)
          companyRaw = splitDomain(1)

      //make sure its an e-amil
      if (email._2 contains '@') {

        //remove numbers and domain

        println("After shedding domain " + shedDomain)

        //john.jim.doe
        if (shedDomain.contains('.') && !shedDomain.contains('_') && !shedDomain.contains('-') ){
           names = shedDomain.split('.')
        }
          //john-jim-doe
          else if(shedDomain.contains('-') && !shedDomain.contains('.') && !shedDomain.contains('_')) {
          names = shedDomain.split('-')
        }
          //john_jim_doe
        else if (shedDomain.contains('_') && !shedDomain.contains('.') && !shedDomain.contains('-')) {
           names = shedDomain.split('_')
        }
          //john_jim.doe
        else if (shedDomain.contains('_') && shedDomain.contains('.') && !shedDomain.contains('-')) {
           names = shedDomain.split('_').flatMap(word => word.split('.'))
        }
          //john_jim-doe
          else if (shedDomain.contains('_') && shedDomain.contains('-') && !shedDomain.contains('.')) {
          names = shedDomain.split('_').flatMap(word => word.split('-'))
        }
          //john.jim-doe
        else if (shedDomain.contains('.') && shedDomain.contains('-') && !shedDomain.contains('_')) {
          names = shedDomain.split('.').flatMap(word => word.split('-'))
        }
          //john-jim.doe_thesecond
        else if (shedDomain.contains('.') && shedDomain.contains('-') && shedDomain.contains('_')) {
          names = shedDomain.split('.').map(word => word.split('-')).flatten.flatMap(word => word.split('_'))
        }
          //johnjimdoe
        else {
           names = Array(shedDomain, null)
        }
      }
        //not an email or forgot domain
      else {
         names = Array(email._2, null)
      }

      println(names.mkString(", "))

      Person.updateParsed(email._1, names, companyRaw)

  }

}
