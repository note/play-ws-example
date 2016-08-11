package controllers

import akka.NotUsed
import akka.actor._
import akka.pattern.ask
import akka.stream._
import akka.stream.scaladsl.{Flow, Source}
import akka.util.Timeout
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Controller, WebSocket}
import services.Service

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class WebSocketController (implicit serviceA: ActorRef, actorSystem: ActorSystem, materializer: Materializer) extends Controller {
  def index = WebSocket.accept[String, String] { request =>
    val killSwitch = KillSwitches.shared(scala.util.Random.nextInt.toString)
    val flow = Flow[String].map(s => s).via(killSwitch.flow)
    flow.via(ActorFlow.actorRef(out => MyWebSocketActor.props(serviceA, out, killSwitch)))
  }
}

object MyWebSocketActor {
  def props(serviceA: ActorRef, out: ActorRef, killSwitch: SharedKillSwitch) = Props(new MyWebSocketActor(serviceA, out, killSwitch))
  case object Stop
}

class MyWebSocketActor(serviceA: ActorRef, out: ActorRef, killSwitch: SharedKillSwitch) extends Actor {
  implicit val timeout: Timeout = 20.seconds

  def receive = {
    case msg: String =>
      out ! msg
      context.system.scheduler.scheduleOnce(300.millis, self, MyWebSocketActor.Stop)
    case MyWebSocketActor.Stop =>
      killSwitch.shutdown()
      println("bazinga 2")
      self ! PoisonPill
  }

  override def postStop() = {
    println("websocket's actor has been stopped")
  }
}
