package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives.{complete, path, get, post, concat}
import akka.http.scaladsl.{Http, HttpExt}

import scala.concurrent.{ExecutionContext, Future}

object AkkHttpServer extends App {
  implicit val rootSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.ignore, "root")
  implicit val context: ExecutionContext = rootSystem.executionContext

  private val http: HttpExt = Http()

  val route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }



  http.newServerAt("localhost", 8080).bind(route)
}
