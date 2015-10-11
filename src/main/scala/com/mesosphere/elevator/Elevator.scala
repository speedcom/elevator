package com.mesosphere.elevator

import akka.actor._

case class Elevator(id: Int, ref: ActorRef)
