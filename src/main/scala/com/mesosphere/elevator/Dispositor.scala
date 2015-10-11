package com.mesosphere.elevator

import akka.actor._
import com.mesosphere.elevator.ElevatorActor.Direction

object DispositorProtocol {
  sealed trait DispositorMsg
  final case class Request(floor: Int, direction: Direction) extends DispositorMsg
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

  def getCost(floor: Int, direction: Direction) = ElevatorCost.getCost(floor, direction) {
    elevator =>
      (elevator.ref ? ElevatorProtocol.GetCost(floor, direction))
        .mapTo[ElevatorCost.Cost]
        .map(cost => ElevatorCost(elevator, cost))
  }

  def receive = {
    case DispositorProtocol.Request(floor, direction) =>
      val futureTask = new DispositorProcessor()
        .chooseElevator(elevators)(getCost(floor, direction))
        .map(el => (el, floor, direction))
      futureTask pipeTo self
    case (Elevator(id, ref), floor: Int, direction: Direction) =>
      log.info(s"[id, floor, direction] = [$id, $floor, $direction]")
      ref ! ElevatorProtocol.Pickup(floor, direction)
  }

}