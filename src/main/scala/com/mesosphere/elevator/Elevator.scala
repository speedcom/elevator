package com.mesosphere.elevator

import akka.actor.{ ActorRef, FSM, Actor, ActorSystem, Props }
import com.mesosphere.elevator.ElevatorActor._
import com.mesosphere.elevator.ElevatorProtocol._

case class Elevator(id: Int, ref: ActorRef)

object ElevatorProtocol {
  sealed trait ElevatorEvent
  final case class GetCost(floor: Int, direction: Direction) extends ElevatorEvent

  sealed trait Request extends ElevatorEvent {
    def floor: Int
  }
  final case class Pickup(override val floor: Int, direction: Direction) extends Request
  final case class GetOut(override val floor: Int) extends Request
}

object ElevatorActor {
  def props = Props[ElevatorActor]

  sealed trait Direction
  final case object Positive extends Direction
  final case object Negative extends Direction
  final case object Undefined extends Direction

  // states
  sealed trait State
  final case object Idle extends State
  final case object GoingNegative extends State
  final case object GoingPositive extends State
  final case object Open extends State

  // data
  sealed trait Data
  case object Unitialized extends Data
  case class ElevatorData(currentFloor: Int, direction: Direction, requests: Set[Request]) extends Data
}

class ElevatorActor extends FSM[State, Data] {
  import scala.concurrent.duration._

  startWith(Idle, ElevatorData(currentFloor = 0, direction = Undefined, requests = Set.empty[Request]))

  when(Idle) {
    case Event(req: Request, data @ ElevatorData(currentFloor, _, requests)) if req.floor < currentFloor =>
      goto(GoingNegative) using data.copy(direction = Negative, requests = requests + req)
    case Event(req: Request, data @ ElevatorData(currentFloor, _, requests)) if req.floor > currentFloor =>
      goto(GoingPositive) using data.copy(direction = Positive, requests = requests + req)
    case Event(req: Request, data @ ElevatorData(currentFloor, _, requests)) if req.floor == currentFloor =>
      goto(Open)
  }

  when(Open, 4 seconds) {
    case Event(StateTimeout, data @ ElevatorData(currentFloor, direction, requests)) =>
      if (direction == Positive && stillHasRequestForUpperFloor(currentFloor, requests)) {
        goto(GoingPositive)
      } else if (direction == Negative && stillHasRequestForLowerFloor(currentFloor, requests)) {
        goto(GoingNegative)
      } else {
        if (stillHasRequestForUpperFloor(currentFloor, requests))
          goto(GoingPositive) using data.copy(direction = Positive)
        else if (stillHasRequestForLowerFloor(currentFloor, requests))
          goto(GoingNegative) using data.copy(direction = Negative)
        else
          goto(Idle) using data.copy(requests = Set.empty[Request])
      }
    case Event(req: Request, data @ ElevatorData(currentFloor, Undefined, requests)) if req.floor < currentFloor =>
      stay() using data.copy(direction = Negative, requests = requests + req)
    case Event(req: Request, data @ ElevatorData(currentFloor, Undefined, requests)) if req.floor > currentFloor =>
      stay() using data.copy(direction = Positive, requests = requests + req)
    case Event(req: Request, data @ ElevatorData(_, _, requests)) =>
      stay() using data.copy(requests = requests + req)
  }

  whenUnhandled {
    case Event(getCost: GetCost, ElevatorData(_, _, requests)) =>
      sender() ! ElevatorCost.Cost(requests.size) // naive implementation - should based on direction and numbers of requests
      stay()
  }

  def stillHasRequestForLowerFloor(floor: Int, requests: Set[Request]): Boolean = requests.exists(_.floor < floor)
  def stillHasRequestForUpperFloor(floor: Int, requests: Set[Request]): Boolean = requests.exists(_.floor > floor)

  initialize()
}
