package GrpcType
//import RestApi.delta
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import akka.stream.Materializer
import com.google.gson.Gson
import scala.language.postfixOps
import com.typesafe.config.ConfigFactory

import java.util.logging.Logger
import scala.concurrent.duration.{DurationLong, FiniteDuration}
import scala.concurrent.{Await, Future}

class Lambda

case class SearchClass(time: String, delta: String){
}

case class queryStringParam(queryStringParameters: SearchClass)


object Lambda {

  // Akka creates Actors
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  import system.dispatcher

  private[this] val logger = Logger.getLogger(classOf[Lambda].getName)


  // Driver function waits for the predefined time to get the complete future string return value from the sendRequest method.
  def apply(time: String, delta: String): String = {
    logger.info("Started Lambda execution\n" +
      "delta\t: " + delta + "" +
      "time\t:" + time)
    logger.info("Calling sendRequest")
    Await.result(sendRequest(time, delta), 5 seconds)
  }

  // funtion to generate String object to store incoming values

  def sendRequest(time: String,delta: String): Future[String] = {
    val uri = "https://ts2374b3l0.execute-api.us-east-2.amazonaws.com/test/mylambda?" + "&time=" + time + "&delta=" + delta
    val payload       = new SearchClass(time, delta)
    val query = new queryStringParam(payload)
    val payloadAsJson = new Gson().toJson(query)
    println(payloadAsJson)
    // HTTP Request
    val req  = HttpRequest(method = HttpMethods.POST, uri = s"$uri", entity = HttpEntity(ContentTypes.`application/json`, payloadAsJson))
    // HTTP Response
    val responseFuture: Future[HttpResponse] = Http().singleRequest(req)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(5 seconds))
    logger.info("Returning from sendRequest")
    entityFuture.map(entity => entity.data.utf8String)
  }

}