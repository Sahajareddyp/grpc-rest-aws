package RestType

// package RestType
import GrpcType.{SearchClass, queryStringParam}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import akka.stream.Materializer
import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import scala.language.postfixOps

import java.util.logging.Logger
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.concurrent.{Await, Future}

class RestApi

object RestApi {

  //  Akka objects to create the ActorSystem object and its derivatives for the communication.
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  import system.dispatcher // "thread pool"
  // Read values from configuration file
  val config: Config            = ConfigFactory.load()
  val value: Int                = config.getInt("parameters.value")
  val time: String              = config.getString("parameters.time")
  val delta: String            = config.getString("parameters.delta")
  val key: String               = config.getString("parameters.key")
  val timeout: FiniteDuration   = 5 seconds
  // intialize logger
  private[this] val logger = Logger.getLogger(classOf[RestApi].getName)

  def sendRequest(): Future[String] = {
    // form the requests to sent to API Gateway
    logger.info("Sending Request")
    //val responseFuture: Future[HttpResponse] = Http().singleRequest(Get(s"https://a94he0mou3.execute-api.us-east-2.amazonaws.com/default/bucketextract?range=$range&time=$time&bucket=$bucket&key=$key"))
    val uri = "https://ts2374b3l0.execute-api.us-east-2.amazonaws.com/test/mylambda?" + "&time=" + time + "&delta=" + delta
    val payload = new SearchClass(time, delta)
    val query = new queryStringParam(payload)
    val payloadAsJson = new Gson().toJson(query)
    println(payloadAsJson)
    val req = HttpRequest(method = HttpMethods.POST, uri = s"$uri", entity = HttpEntity(ContentTypes.`application/json`, payloadAsJson))
  // send the request to API Gateway
    val responseFuture: Future[HttpResponse] = Http().singleRequest(req)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(timeout))
    logger.info("Sent Request")
    println(responseFuture.toString)
    entityFuture.map(entity => entity.data.utf8String)
  }
  def main(args: Array[String]): Unit ={

    logger.info("Started execution")
    System.out.println("\n\n" + Await.result(sendRequest(), timeout) + "\n\n")
    logger.info("Completed successfully")
  }
}