# Elevator System

## Technology - Scala & AkkaFSM
Elevator is a good example of Finithe State Machine that is why I decided to use AkkaFSM module.
Both Scala and AkkaFSM's DSL make program really short and easy to reason about.
Motivation:
- concurrency (multiple working Elevators and Dispositor)
- immutability
- nice DSL (reasoning about program)
- test coverage (unit + spec tests)

## Algorithm
It consists of two main part:
1. Dispositor
2. Elevators states transition

Dispositor is responsible for distribution of PickUp requests between Elevators.
Decision alghoritm base on the Cost of every Elevators and choose with the smallest one.
This is done in asynchronous/non-blocking manner.

Elevators state transition - every request is buffered per Elevator so it knows what next step need to be done.

Elevator can be in following states: Idle, Open, GoingPositive, GoingNegative

## Environment
You need to have installed `sbt` tool in case of running/testing program.

run app: ```sbt run```

test app: `sbt test`
