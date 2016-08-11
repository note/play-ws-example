package services

import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.duration._

class Service(latencyMillis: Int, respondWith: String) extends Actor {
  import Service._
  implicit val ec = context.system.dispatcher

  override def receive: Receive = {
    case Request =>
      context.system.scheduler.scheduleOnce(latencyMillis.millis, self, TimeToRespond(sender()))

    case TimeToRespond(originalSender) =>
      println("bazinga: " + respondWith)
      originalSender ! respondWith
  }
}

object Service {
  case object Request
  case class TimeToRespond(originalSender: ActorRef)

  def props(latencyMillis: Int, respondWith: String) = Props(new Service(latencyMillis, respondWith))
}
