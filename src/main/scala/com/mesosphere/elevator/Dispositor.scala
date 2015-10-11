package com.mesosphere.elevator

import akka.actor._

object DispositorProtocol {
  sealed trait DispositorMsg
  final case class Request(floor: Int) extends DispositorMsg
}
object DispositorActor {
  def props(elevators: Seq[Elevator]) = Props(new DispositorActor(elevators))
}

// TODO: use scalaz.NonEmptyList instead of Seq
class DispositorActor(private val elevators: Seq[Elevator]) extends Actor {
  import scala.concurrent.duration._
  import scala.concurrent.{ Future, ExecutionContext }
  import akka.util.Timeout
  import akka.pattern.ask
  import akka.pattern.pipe
  import akka.event.Logging

  implicit val timeout = Timeout(1 second)
  implicit val executionContext = context.dispatcher

  val log = Logging(context.system, this)

  def getCost(floor: Int) = ElevatorCost.getCost(floor) {
    elevator =>
      (elevator.ref ? ElevatorProtocol.GetCost(floor))
        .mapTo[ElevatorCost.Cost]
        .map(cost => ElevatorCost(elevator, cost))
  }

  def receive = {
    case DispositorProtocol.Request(floor) =>
      val futureTask = new DispositorProcessor()
        .chooseElevator(elevators)(getCost(floor))
        .map(el => (el, floor))
      futureTask pipeTo self
    case (Elevator(id, ref), floor: Int) =>
      log.info(s"[id, floor] = [$id, $floor]")
      ref ! ElevatorProtocol.Pickup(floor)
  }

}