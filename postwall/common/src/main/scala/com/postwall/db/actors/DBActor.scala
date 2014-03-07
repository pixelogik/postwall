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

package com.postwall.db.actors

// Akka basics
import akka.actor.Actor

// MongoDB driver
import com.mongodb.casbah.Imports._

// Import DAO trait
import com.postwall.db.PostwallDAO
import com.postwall.db.Helpers._

// This objects holds case objects and classes for DB actor messages ("commands")
object DBActor {
  case object Pling
  case object GetPosts
  case class  PostToWall(params: WallPostParams)
}

// The actual actor doing the DB access
class DBActor extends Actor with PostwallDAO {
  import DBActor._

  println("Connecting to MongoDB at "+mongoURI)

  // The actor communicates with the outside world only via this method
  def receive: Receive = {
    case Pling =>
      sender ! "PLONG"

    case GetPosts =>
      sender ! getAllPosts()

    case PostToWall(params: WallPostParams) =>
      sender ! postToWall(params)

  }
}





