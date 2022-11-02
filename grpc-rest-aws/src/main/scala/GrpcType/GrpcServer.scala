package GrpcType
//import com.example.protos.log.LogFinderGrpc
import scala.language.postfixOps
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory
import log.{GreeterGrpc, LambdaReply, LambdaRequest}
import io.grpc.{Server, ServerBuilder}
import scala.language.postfixOps
import java.util.logging.Logger
import scala.concurrent.{ExecutionContext, Future}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
object grpcServer{

  private val logger = Logger.getLogger(classOf[grpcServer].getName)
  private val port = ConfigFactory.load().getInt("parameters.port")

  def main(args: Array[String]): Unit = {

    logger.info("Starting grpcServer.")
    val server = new grpcServer(ExecutionContext.global)
    server.start()
    server.blockUntilShutdown()

  }
}

class grpcServer(executionContext: ExecutionContext) {self =>

  private[this] var server: Server = null
  private val logger = Logger.getLogger(classOf[grpcServer].getName)

  // Start the server
  private def start(): Unit = {
    server = ServerBuilder.forPort(grpcServer.port).addService(GreeterGrpc.bindService(new GreeterImpl, executionContext)).build.start
    logger.info("Server started, listening on " + grpcServer.port)
    sys.addShutdownHook {
      System.err.println("shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("server shut down")
    }
  }

  private def stop(): Unit =
    if (server != null) server.shutdown()

  private def blockUntilShutdown(): Unit =
    if (server != null) server.awaitTermination()

  // function calls lambda function on aws

  private class GreeterImpl extends GreeterGrpc.Greeter {
    override def findLog(req: LambdaRequest): Future[LambdaReply] = {
      val exec = Lambda(req.time, req.bucket)
      val reply = LambdaReply(exec)
      Future.successful(reply)
    }
  }
}