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

package com.postwall.api.utils

// Marshalling
import spray.httpx.marshalling._
import spray.json._
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol._
import spray.httpx.SprayJsonSupport._

// DB entities
import com.postwall.db._
import com.postwall.db.Helpers._

// DateTime support
import org.joda.time.DateTime

// MongoDB driver
import com.mongodb.casbah.Imports._

object Marshallers {

  // Define JSON marshallers (they do serialization and deserialization) for all entities and helper case classes used by API
  trait JsonMarshallers extends DefaultJsonProtocol with SprayJsonSupport {
    implicit object dateTimeFormat extends DateTimeJsonFormat
    implicit object objectIdFormat extends ObjectIdJsonFormat
    implicit val WallPostParamsFormat = jsonFormat2(WallPostParams)
    implicit val WallPostFormat = jsonFormat3(WallPost)
    implicit val APIErrorFormat = jsonFormat2(APIError)
  }

  // This trait specifies how DateTime values are written and read
  trait DateTimeJsonFormat extends JsonFormat[DateTime] {
    private val dateTimeFmt = org.joda.time.format.ISODateTimeFormat.dateTime
    def write(x: DateTime) = JsString(dateTimeFmt.print(x))
    def read(value: JsValue) = value match {
      case JsString(x) => dateTimeFmt.parseDateTime(x)
      case x => deserializationError("Expected DateTime as JsString, but got " + x)
    }
  }

  // This trait specifies how mongo's ObjectId values are written and read
  trait ObjectIdJsonFormat extends JsonFormat[ObjectId] {
    def write(x: ObjectId) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => new ObjectId(x)
      case x => deserializationError("Expected ObjectId as JsString, but got " + x)
    }
  }

}
