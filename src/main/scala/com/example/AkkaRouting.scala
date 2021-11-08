//#full-example
package com.example

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.{Behaviors, Routers}

import java.time.Instant
import scala.concurrent.duration.{FiniteDuration, SECONDS}

case class Work(work: String, status: Int, finalWorkRecipient: ActorRef[Result])
case class Result(result: String)

//                       Logger
//                  /      |       \
//              worker1 worker2 worker3
// Coordinator     |      |       |

object CoordinatorActor {

  def apply() : Behavior[Work] = Behaviors.setup(ctx => {
    val pool = Routers.pool(poolSize = 4) {
      // make sure the workers are restarted if they fail
      Behaviors.supervise(Worker()).onFailure[Exception](SupervisorStrategy.restart)
    }
    val router = ctx.spawn(pool, "worker-pool")

    Behaviors.receiveMessagePartial {
      case Work(work, 0, actorRef) =>
        router ! Work(work, 0, actorRef)

        Behaviors.same
    }
  })
}

object Worker {
  def apply(): Behavior[Work] = Behaviors.withTimers(
    timer => Behaviors.receiveMessage {
      case Work(work, 0, recipient) =>
        println(s"Received Work[$work]. Processing...")
        timer.startSingleTimer(Work(work, 1, recipient), FiniteDuration(1, SECONDS))
        Behaviors.same
      case Work(work, 1, recipient) =>
        recipient ! Result(s"$work is done")
        Behaviors.same
    }
  )
}

object Logger {
  def apply() : Behavior[Result] = Behaviors.receiveMessage {
    result => {
      println(s"Logging: [${Instant.now}] $result")

      Behaviors.same
    }
  }
}


//#main-class
object AkkaRouting extends App {
  //#actor-system
  val systemActor: ActorSystem[Work] = ActorSystem(CoordinatorActor(), "AkkaRouting")
  //#actor-system

  val loggerActor = systemActor.systemActorOf(Logger(), "AkkaLogger")

  //#main-send-messages

  Range(0, 100).foreach(id => systemActor ! Work(s"Work $id", 0, loggerActor))

  //#main-send-messages
}
//#main-class
//#full-example
