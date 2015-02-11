package Actors

import akka.actor.{Props, Actor}
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._


class GeoLookup extends Actor {


  def receive = {
    case ip: (Long, String) =>
      println("IP recevied by GeoLookup " + ip)
      context.stop(self)
  }
}
