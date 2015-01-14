package models

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ResultSetFuture
import com.datastax.driver.core.Session
import scala.collection.JavaConversions._
import play.api.Logger
import com.datastax.driver.core.Metadata

//the SimpleClient is the first step in connecting to cassandra, it is defined in
//http://www.datastax.com/documentation/developer/java-driver/2.0/java-driver/quick_start/qsSimpleClientCreate_t.html
//this is a Scala equivalent of that

class SimpleClient(node: String) {

  private val cluster = Cluster.builder().addContactPoint(node).build()
  log(cluster.getMetadata())
  val session = cluster.connect()

  private def log(metadata: Metadata): Unit = {
    Logger.info(s"Connected to cluster: ${metadata.getClusterName}")
    for (host <- metadata.getAllHosts()) {
      Logger.info(s"Datatacenter: ${host.getDatacenter()}; Host: ${host.getAddress()}; Rack: ${host.getRack()}")
    }
  }
//build a query to return all the rows
  def getRows: ResultSetFuture = {
    val query = QueryBuilder.select().all().from("ipaddresses", "timeonpage")
    session.executeAsync(query)
  }

  def close() {
    session.close
    cluster.close
  }

}
//connect
  object Cassandra extends App {
    val client = new SimpleClient("127.0.0.1")
    //println(client.getRows)
    client.close
  }
