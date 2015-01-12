package models

import scala.collection.convert.WrapAsScala
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Row

case class timeOnPage(ip: String, pages: Map[String, Option])

class displayTimes(client: SimpleClient) {


  import Utils._

  def getAll(implicit ctxt: ExecutionContext): Future[(timeOnPage)] = {

    import WrapAsScala.iterableAsScalaIterable

    // you can't toList a (String, Map) I don't think, need some other way of converting to a scala collection

    client.getRows.toScalaFuture.map { rows =>
      rows.map(row => times(row)).toList
    }
  }

  //need to figure out how to use getMap correctly

  private def times(row: Row): timeOnPage =
    timeOnPage(row.getString("ip"), row.getMap("times"))
}
  //may need this to convert the rows to JSON
object JsonFormats {
//  implicit val songFormat: Format[Song] = Json.format[Song]
//  implicit val songDataReads = (
//    (__ \ 'title).read[String] and
//      (__ \ 'album).read[String] and
//      (__ \ 'artist).read[String]) tupled

}