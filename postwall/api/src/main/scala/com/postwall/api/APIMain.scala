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

package com.postwall

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

import com.postwall.api.actors.APIServiceActor

object APIMain extends App {

  // Set host and port (get port from Heroku environment, if set)
  val host = "0.0.0.0"
  val port = Option(System.getenv("PORT")).getOrElse("8080").toInt

  println("Binding Postwall API service actor to port "+port)

  // We need an ActorSystem to host our application in
  implicit val system = ActorSystem("postwall-actors")

  // Create and start our service actor
  val service = system.actorOf(Props[APIServiceActor], "postwall-service")

  // Start a new HTTP server on the selected port with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = host, port = port)
}

