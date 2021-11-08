//#full-example
package com.example


import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.dispatch.Dispatchers

object WordConcatenator {

  case class Word(message: String)

  def apply(state: String) : Behavior[Word] = Behaviors.logMessages(
    Behaviors.receiveMessage {
      message: Word => {
        println(s"Incoming Message $message")
        println(s"Current State $state")

        val nextState = s"$state _ ${message.message}"
        WordConcatenator(nextState)
      }
    }
  )
}


//#main-class
object AkkaQuickstart extends App {
  //#actor-system
  val akkaWordConcatenator: ActorSystem[WordConcatenator.Word] = ActorSystem(WordConcatenator(""), "AkkaWordConcatenator")
  //#actor-system



  //#main-send-messages
  akkaWordConcatenator ! WordConcatenator.Word("Hello World")
  akkaWordConcatenator ! WordConcatenator.Word("Same")
  akkaWordConcatenator ! WordConcatenator.Word("Bob")
  //#main-send-messages
}
//#main-class
//#full-example
