package com.mesosphere.elevator

import org.scalatest.Matchers
import org.scalatest.WordSpecLike
import org.scalatest.BeforeAndAfterAll
import scala.concurrent.Future
import scala.util.Success

class DispositorProcessorTest extends WordSpecLike with Matchers {
  import scala.concurrent.ExecutionContext.Implicits.global

  "A DispositorProcessor" must {

    "choose Elevator with lowest cost from sequence of them" in {
      // before
      val elevator1 = Elevator(id = 1, ref = null)
      val elevator2 = Elevator(id = 2, ref = null)
      val elevator3 = Elevator(id = 3, ref = null)
      val elevators = Seq(elevator1, elevator2, elevator3)

      // when
      val elevatorWithLowestCost = new DispositorProcessor().chooseElevator(elevators) {
        elevator =>
          if (elevator.id == 1)
            Future.successful(ElevatorCost(elevator, cost = ElevatorCost.Cost(1000)))
          else if (elevator.id == 2)
            Future.successful(ElevatorCost(elevator, cost = ElevatorCost.Cost(1)))
          else
            Future.successful(ElevatorCost(elevator, cost = ElevatorCost.Cost(50)))
      }

      // then
      elevatorWithLowestCost.onComplete { _ shouldBe Success(elevator2) }
    }

  }

}