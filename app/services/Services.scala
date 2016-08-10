package services

import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class Service(latencyMillis: Int) extends Actor {
  import Service._

  override def receive: Receive = {
    case Request =>
      context.system.scheduler.scheduleOnce(latencyMillis.millis, self, TimeToRespond(sender()))

    case TimeToRespond(originalSender) =>
      originalSender ! scala.util.Random.nextInt(100)
  }
}

object Service {
  case object Request
  case class TimeToRespond(originalSender: ActorRef)

  def props(latencyMillis: Int) = Props(new Service(latencyMillis))
}
