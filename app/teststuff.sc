def insertLocation(ip: String): String = {
  val url = "http://ip-api.com/json/24.22.84.77"
  val result = scala.io.Source.fromURL(url).mkString
  println(result)
  result
}