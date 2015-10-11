package com.mesosphere.elevator

import akka.actor._

case class Elevator(id: Int, ref: ActorRef)

object ElevatorProtocol {
  sealed trait ElevatorMsg
  final case class GetCost(floor: Int) extends ElevatorMsg
  final case class Pickup(floor: Int) extends ElevatorMsg
}

object ElevatorActor {
  def props = Props[ElevatorActor]
}
class ElevatorActor extends Actor {

  def receive = {
    case ElevatorProtocol.GetCost(floor) => sender() ! ElevatorCost.Cost(scala.util.Random.nextInt % 100) // naive implementation
    case ElevatorProtocol.Pickup(floor) => ???
  }

}