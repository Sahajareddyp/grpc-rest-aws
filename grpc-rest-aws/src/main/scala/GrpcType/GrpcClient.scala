package GrpcType
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.config.{Config, ConfigFactory}
import log.{GreeterGrpc, LambdaRequest}
import log.GreeterGrpc.GreeterBlockingStub
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import scala.language.postfixOps
import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}

import scala.concurrent.{Await, Future}
import akka.stream.Materializer

import scala.concurrent.duration.DurationInt
//import io.grpc.examples.helloworld.log.GreeterGrpc.GreeterBlockingStub
//import io.grpc.examples.helloworld.log.{GreeterGrpc, LambdaRequest}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import scala.language.postfixOps
final case class lambdarequest(time: String,delta: String, pattern: String)
object grpcClient {

  // Read values from application.conf
  val config: Config  = ConfigFactory.load()
  val value: Int      = config.getInt("parameters.value")
  val time: String    = config.getString("parameters.time")
  val bucket: String  = config.getString("parameters.delta")
  // val key: String     = config.getString("parameters.key")
  val port: Int       = config.getInt("parameters.port")
  // initialize logger
  private[this] val logger = Logger.getLogger(classOf[grpcClient].getName)
  // driver to make the class runnable, host- local host, port- defined in application.conf file
  def apply(host: String, port: Int): grpcClient = {
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build
    val blockingStub = GreeterGrpc.blockingStub(channel)
    new grpcClient(channel, blockingStub)
  }
  // driver function
  def main(args: Array[String]): Unit = {
    logger.info("Starting grpcClient:" + "" +
      "port\t: " + port)
    val client = grpcClient("localhost", port)
    try client.find(time, bucket)
    finally client.shutdown()
  }
}

class grpcClient private(private val channel: ManagedChannel, private val blockingStub: GreeterBlockingStub) {
  private[this] val logger = Logger.getLogger(classOf[grpcClient].getName)
  private implicit val formats = DefaultFormats
  def shutdown(): Unit = {
    logger.info("Trying to shutdown")
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS) // keeping the timeout as 5 seconds
  }

  // Main function which processes the response from grpcServer and outputs log statements according to the result.
  def find(time: String, bucket: String): Unit = {
    //  Akka objects to create the ActorSystem object and its derivatives for the communication.
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: Materializer = Materializer(system)
    import system.dispatcher // "thread pool"
    val request = LambdaRequest(time, bucket)
    // Load Custom Congigurations
    val config: Config  = ConfigFactory.load()
    val pattern: String = config.getString("parameters.pattern")
    logger.info("Request created")
    try {
      val response = blockingStub.findLog(request)
      logger.info(response.toString)
      println(response.result.toString)
      if(response.result.equals("{\"statusCode\": 200, \"body\": \"\\\"Logs found within provided time and delta!\\\"\"}"))
       {
          val lambda2= "https://6xyybsa2gb.execute-api.us-east-2.amazonaws.com/test/mylambda2"
          val lambdain: lambdarequest = lambdarequest(time,bucket,pattern)
          val requestentity = write(lambdain)
          // HTTP request
          val call=HttpRequest(
            method = HttpMethods.POST,
            uri=s"${lambda2}",
            entity = HttpEntity(requestentity))
          val responseFuture = Http().singleRequest(call)
          // HTTP response
          val response = Await.result(responseFuture.flatMap(_.entity.toStrict(60 seconds)).map(_.data.utf8String),61 seconds)
          logger.info("Sent Request")
          // entityFuture.map(entity => entity.data.utf8String)
          println(response)
        }
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }
}
