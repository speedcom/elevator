package com.mesosphere.elevator

import akka.actor._
import akka.actor.FSM.{ Transition, CurrentState, SubscribeTransitionCallBack }
import akka.testkit._
import org.scalatest.{ MustMatchers, FunSpecLike, BeforeAndAfterAll }
import com.mesosphere.elevator.ElevatorActor._
import com.mesosphere.elevator.ElevatorProtocol._

class ElevatorActorSpec extends TestKit(ActorSystem("test-system"))
    with MustMatchers
    with FunSpecLike
    with BeforeAndAfterAll
    with ImplicitSender {

  override protected def afterAll() {
    super.afterAll()
    system.shutdown()
  }

  describe("An elevator") {

    it("should transition to Open state when get request with floor the same as Elevator is") {
      val elevator = TestActorRef(Props(new ElevatorActor))

      elevator ! SubscribeTransitionCallBack(testActor)

      expectMsgPF() {
        case CurrentState(elevator, Idle) => true
      }

      elevator ! Pickup(0, Positive)

      expectMsg(Transition(elevator, Idle, Open))
    }
  }

}