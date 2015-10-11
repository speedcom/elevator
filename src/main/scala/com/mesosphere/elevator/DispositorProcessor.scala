package com.mesosphere.elevator

import scala.concurrent.{ Future, ExecutionContext }

class DispositorProcessor {
  def chooseElevator(elevators: Seq[Elevator])(getCost: Elevator => Future[ElevatorCost])(implicit ec: ExecutionContext): Future[Elevator] = {
    Future
      .sequence(elevators.map(getCost))
      .map { _.minBy(_.cost).elevator }
  }
}