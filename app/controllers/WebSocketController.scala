package controllers

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import play.api.mvc.{Action, Controller}
import services.Service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class WebSocketController (actorSystem: ActorSystem) extends Controller {
  implicit val timeout: Timeout = 20.seconds

  def index = Action.async {
    val serviceA = actorSystem.actorOf(Service.props(1200))
    val response = (serviceA ? Service.Request).mapTo[Int]
    response.map(r => Ok("hello world" + r))
  }
}
