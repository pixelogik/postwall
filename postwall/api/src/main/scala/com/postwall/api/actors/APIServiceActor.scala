//
// Copyright (c) 2014 Ole Krause-Sparmann
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.postwall.api.actors

// Akka basics
import akka.actor._
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask

// Spray routing and HTTP
import spray.routing.Route._
import spray.routing._
import spray.http._
import reflect.ClassTag

// Scala implicits and futures
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// Classes for timeout setup
import akka.util.Timeout
import scala.concurrent.duration._

// DB actor, commands and model case classes
import com.postwall.db.actors.DBActor
import com.postwall.db._
import com.postwall.db.Helpers._

import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._

// DateTime support
import org.joda.time.DateTime

// Marshallers for db entities and other case classes
import com.postwall.api.utils.Marshallers._

// We don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class APIServiceActor extends Actor with PostwallService {

  println("Postwall API service actor started")

  // Set implicit execution context and set timeout
  implicit def executionContext = actorRefFactory.dispatcher

  // Get actor context
  def actorRefFactory = context

  // Create DB worker actor
  val dbWorker = context.actorOf(Props[DBActor], "db-worker")

  // Return db actor for PostwallService trait routes
  def dbWorkerObject: ActorRef = dbWorker

  // This actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(routes)
}

// this trait defines our service behavior independently from the service actor
trait PostwallService extends HttpService with JsonMarshallers {
  // Timeout used by futures (? pattern)
  implicit val timeout = Timeout(60 seconds)

  // Returns db actor
  def dbWorkerObject: ActorRef

  // We use detach() on each request handler. This executes the request processing in a separate actor thus
  // does not block the service actor.
  val routes = {
    // Wrap all directives in exception handler directive
    path("ping") {
      get {
        detach() {
          complete("pong")
        }
      }
    }~
    path("posts") {
      post {
        // Parse WallPostParams entity from JSON
        entity(as[WallPostParams]) { params =>
          // Process in another actor (do not block this service actor)
          detach() {
            handleExceptions( ExceptionHandler {case _: Exception => complete(StatusCodes.BadRequest, APIError(errorCode=APIErrorCodes.UNKNOWN_ERROR, errorDescripton="Could not post"))} ) {
              // Ask (?) db actor for result and map that future to Option[WallPost] and call get on it to get the actual post.
              complete( (dbWorkerObject ? DBActor.PostToWall(params)).mapTo[Option[WallPost]].map(x => x.get))
            }
          }
        }
      }~
      get {
        detach() {
          handleExceptions( ExceptionHandler {case _: Exception => complete(StatusCodes.BadRequest, APIError(errorCode=APIErrorCodes.UNKNOWN_ERROR, errorDescripton="Could not get posts"))} ) {
            // Ask (?) db actor for result and map that future to Option[List[WallPost]] and call get on it to get the actual list.
            complete( (dbWorkerObject ? DBActor.GetPosts).mapTo[Option[List[WallPost]]].map(x => x.get))
          }
        }
      }
    }
  }

}
