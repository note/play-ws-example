package controllers

import akka.actor._
import akka.stream._
import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.mvc.{Controller, WebSocket}

class WebSocketController (implicit serviceA: ActorRef, actorSystem: ActorSystem, materializer: Materializer) extends Controller {
  val items = List(
    "abc",
    "def",
    scala.util.Random.alphanumeric.take(300).mkString
  )

  def index = WebSocket.accept[String, String] { request =>
    Flow.fromSinkAndSource(Sink.ignore, Source(items))
  }
}
