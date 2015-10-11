package com.mesosphere.elevator

import akka.actor._
import com.mesosphere.elevator.ElevatorActor._
import com.mesosphere.elevator.ElevatorProtocol._

case class Elevator(id: Int, ref: ActorRef)

object ElevatorProtocol {
  sealed trait ElevatorEvent
  final case class GetCost(floor: Int, direction: Direction) extends ElevatorEvent

  sealed trait Request { def floor: Int }
  final case class Pickup(override val floor: Int, direction: Direction) extends ElevatorEvent with Request
  final case class GetOut(override val floor: Int) extends ElevatorEvent with Request
}

object ElevatorActor {
  def props = Props[ElevatorActor]

  sealed trait Direction
  final case object Positive extends Direction
  final case object Negative extends Direction
  final case object NoDirection extends Direction

  // states
  sealed trait State
  final case object Idle extends State

  // data
  sealed trait Data
  final case class ElevatorData(currentFloor: Int, direction: Direction, requests: Set[Request]) extends Data
}
class ElevatorActor extends FSM[State, Data] {

  startWith(Idle, ElevatorData(currentFloor = 0, direction = NoDirection, requests = Set.empty[Request]))

  when(Idle) {
    ???
  }

  initialize()
}