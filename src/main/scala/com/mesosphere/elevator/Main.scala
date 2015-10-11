package com.mesosphere.elevator

import akka.actor._

object Main extends App {

  implicit val actorSystem = ActorSystem("elevator-system")

  val elevators = (1 to 16).map { id =>
    Elevator(id = id, actorSystem.actorOf(ElevatorActor.props))
  }

  val dispositorRef = actorSystem.actorOf(DispositorActor.props(elevators))

  dispositorRef ! DispositorProtocol.Request(floor = 10)

  Thread.sleep(4000)
  actorSystem.shutdown()
}