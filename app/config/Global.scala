package config

import anorm._
import play.api.db._
import models.SimpleClient
import models.displayTimes
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import play.api.Application
import play.api.GlobalSettings

import models.Role._
import models.Account

import play.api.Logger
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Result
import akka.actor.Props
import play.api.libs.concurrent.Akka


import scala.concurrent.Future


////this code produces accesslogs to logs/accesslog.log. Also requires logger.xml in conf/
//object AccessLoggingFilter extends Filter {
//
////  val dateTimeFormat = ISODateTimeFormat.ordinalDateTimeNoMillis()
////
////  def apply(next: EssentialAction) = new EssentialAction {
////    def apply(rh: RequestHeader) = {
////      val startTime = System.currentTimeMillis()
////
////      def logTime(result: Result): Result = {
////        val event = Json.obj(
////          "uri" -> rh.uri,
////          "method" -> rh.method,
////          "timestamp" -> dateTimeFormat.print(new DateTime),
////          "execution_time" -> (System.currentTimeMillis() - startTime),
////          "status" -> result.header.status,
////          "tags" -> Json.toJson(rh.tags.map(entry => entry._1.toLowerCase -> entry._2))
////        )
////        Logger("accesslog").info(Json.stringify(event))
////        result
////      }
////
////      next(rh).map {
////        case plain: Result => logTime(plain)
////       case async: Future[Result] => async.transform(logTime)
////      }
////    }
////  }
//
//
//  //def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
//    def apply(nextFilter: (RequestHeader) => Future[Result])
//             (requestHeader: RequestHeader): Future[Result] = {
//    val startTime = System.currentTimeMillis
//    nextFilter(requestHeader).map { result =>
//      val endTime = System.currentTimeMillis
//      val requestTime = endTime - startTime
//      Logger("accesslog").info(s"${requestHeader.method} ${requestHeader.uri} " +
//        s"took ${requestTime}ms and returned ${result.header.status}")
//      result.withHeaders("Request-Time" -> requestTime.toString)
//    }
////        val msg =
////          s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress} " +
////          s"domain=${request.domain} query-string=${request.rawQueryString} " +
////        s"referer=${request.headers.get("referer").getOrElse("N/A")} " +
////        s"header=${request.headers}"
////        //+
////    //    s"user-agent=[${request.headers.get("user-agent").getOrElse("N/A")}]"
////        Logger("accesslog").info(msg)
////        next(request)
//
////    val result = next(request)
////    Logger("accesslog").info(request.toString() + "\n\t => " + result.toString())
////    result
//
//  }
//}

//These override settings are required because the controller is a scala class, not a scala object
object Global extends GlobalSettings {   //WithFilters(AccessLoggingFilter)//


  var cassandra: SimpleClient = _
  private var controller: controllers.Analytics = _

  override def onStart(app: Application) {

//    cassandra = new SimpleClient(app.configuration.getString("cassandra.node")
//      .getOrElse(throw new IllegalArgumentException("No 'cassandra.node' config found.")))
//    controller = new controllers.Analytics(new displayTimes(cassandra))

    //create some accounts for /admin
    if (Account.findAll.isEmpty) {
      Seq(
        Account(0, "plamb@snappydata.io", "secret", "Pierce", Administrator)
      ) foreach Account.create
      //lol
    }


  }

  override def getControllerInstance[A](clazz: Class[A]): A = {
    // as simple as possible, nothing else needed for now...
    if(clazz == classOf[controllers.Analytics])
      controller.asInstanceOf[A]
    else
      throw new IllegalArgumentException(s"Controller of class $clazz not yet supported")
  }

  override def onStop(app: Application) {
    cassandra.close
  }

  var ip: String = _
  //grabs the IP of any POST request
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if (request.method == "POST") {
     ip = request.remoteAddress
      println("Email input from this IP " + request.remoteAddress)
      super.onRouteRequest(request)
    } else {
      super.onRouteRequest(request)
    }
  }



}

