package com.mesosphere.elevator

import akka.actor._
import com.mesosphere.elevator.ElevatorActor._

object Main extends App {

  implicit val actorSystem = ActorSystem("elevator-actor-system")

  val elevators = (1 to 10).map { id =>
    Elevator(id = id, actorSystem.actorOf(ElevatorActor.props))
  }

  val dispositorRef = actorSystem.actorOf(DispositorActor.props(elevators))

  dispositorRef ! DispositorProtocol.Request(floor = 0, direction = Positive)

  Thread.sleep(4000)
  actorSystem.shutdown()
}