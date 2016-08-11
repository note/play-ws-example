package controllers

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.util.Timeout
import play.api.mvc.{Controller, WebSocket}
import akka.pattern.ask
import services.Service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class WebSocketController (serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef)(implicit actorSystem: ActorSystem, materializer: Materializer) extends Controller {
  implicit val timeout: Timeout = 20.seconds

  def index = WebSocket.accept[String, String] { request =>
    Flow.fromSinkAndSource(Sink.ignore, {
      val respA = Source.fromFuture((serviceA ? Service.Request).mapTo[String])
      val respB = Source.fromFuture((serviceB ? Service.Request).mapTo[String])
      val respC = Source.fromFuture((serviceC ? Service.Request).mapTo[String])
      Source.combine(respA, respB, respC)(Merge(_))
    })
  }
}
