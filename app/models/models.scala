package models

import scala.collection.convert.WrapAsScala
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.collection.JavaConversions._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Row

case class timeOnPage(ip: String, pages: Map[String, Long])

class displayTimes(client: SimpleClient) {


  import Utils._

  def getAll(implicit ctxt: ExecutionContext): Future[List[(timeOnPage)]] = {

    import WrapAsScala.iterableAsScalaIterable

    client.getRows.toScalaFuture.map { rows =>
      rows.map(row => times(row)).toList
    }
  }


  private def times(row: Row): timeOnPage =
    timeOnPage(row.getString("ip"), row.getMap("page", classOf[String], classOf[Long]).toMap)
}

object JsonFormats {

    implicit val timeFormat: Format[timeOnPage] = Json.format[timeOnPage]


}