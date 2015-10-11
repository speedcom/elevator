package com.mesosphere.elevator

import scala.concurrent.{ Future, ExecutionContext }

case class ElevatorCost(elevator: Elevator, cost: ElevatorCost.Cost)

object ElevatorCost {
  def getCost(floor: Int)(f: Elevator => Future[ElevatorCost])(implicit ec: ExecutionContext): Elevator => Future[ElevatorCost] = f

  case class Cost(value: Int)

  implicit object Cost extends Ordering[Cost] {
    def compare(c1: Cost, c2: Cost) = c1.value compare c2.value
  }
}