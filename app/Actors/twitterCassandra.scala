package Actors

import akka.actor.Actor
import models.SimpleClient

/**
 * Created by plamb on 2/27/15.
 */
class twitterCassandra(client: SimpleClient) extends Actor {

  def receive = {
    case screenName: String => {
      println("twitter Actor received, sending: " +screenName)
      client.insertTwitter(screenName)
    }
  }
}
