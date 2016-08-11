package controllers

import akka.NotUsed
import akka.actor._
import akka.pattern.ask
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.Timeout
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Controller, WebSocket}
import services.Service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class WebSocketController (implicit serviceA: ActorRef, actorSystem: ActorSystem, materializer: Materializer) extends Controller {
  def index = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(serviceA, out))
  }
}

object MyWebSocketActor {
  def props(serviceA: ActorRef, out: ActorRef) = Props(new MyWebSocketActor(serviceA, out))
}

class MyWebSocketActor(serviceA: ActorRef, out: ActorRef) extends Actor {
  implicit val timeout: Timeout = 20.seconds

  def receive = {
    case msg: String =>
      out ! "whatever"
      self ! PoisonPill
  }

  override def postStop() = {
    println("websocket's actor has been stopped")
  }
}
