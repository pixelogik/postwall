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

package com.postwall.db

// Salat makes it easy to save case class objects into MongoDB using casbah
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._

// MongoDB driver
import com.mongodb.casbah.Imports._

// Contains DAO definitions for all types in the Postwall model
trait PostwallDAO {
  import EntityCreation._
  import Helpers._

  // Get db connection (get connection string from Heroku env or use localhost)
  val mongoURI = MongoClientURI(Option(System.getenv("MONGOHQ_URL")).getOrElse("mongodb://localhost:27017/postwall"))
  val mongoClient = MongoClient(mongoURI)
  val db = mongoClient(mongoURI.database.get)

  // Collection DAOs ///////////////////////////////////////////////////////////////////////////////////////

  object WallPostDAO extends SalatDAO[WallPost, ObjectId](collection = db("wallposts"))

  // Actual db methods ////////////////////////////////////////////////////////////////////////////////////

  // Returns all posts
  def getAllPosts(): Option[List[WallPost]] = {
    // Get all wall posts
    return Some(WallPostDAO.find(ref = MongoDBObject()).toList)
  }

  // Posts to wall
  def postToWall(params: WallPostParams): Option[WallPost] = {
    val newPost = createPostFromParams(params)
    WallPostDAO.insert(newPost)
    return Some(newPost)
  }

}


