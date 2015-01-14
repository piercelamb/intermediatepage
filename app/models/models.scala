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


//case class that matches the data types stored in cassandra
case class timeOnPage(ip: String, pages: Map[String, Long])

//this class takes a cassandra connection and gives back the cassandra data as scala types
class displayTimes(client: SimpleClient) {


  import Utils._

//a function to get all of the data in a cassandra table, row by row. This will be used in the controller to retrieve data
  def getAll(implicit ctxt: ExecutionContext): Future[List[(timeOnPage)]] = {

    import WrapAsScala.iterableAsScalaIterable

    client.getRows.toScalaFuture.map { rows =>
      rows.map(row => times(row)).toList
    }
  }

//an inner function to convert the data within rows to scala types
  private def times(row: Row): timeOnPage =
    timeOnPage(row.getString("ip"), row.getMap("page", classOf[String], classOf[java.lang.Long]).toMap.asInstanceOf[Map[String,Long]])
}

//this will be used in the controller to take scala data and convert to JSON
object JsonFormats {

    implicit val timeFormat: Format[timeOnPage] = Json.format[timeOnPage]


}