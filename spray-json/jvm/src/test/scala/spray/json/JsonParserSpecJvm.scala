/*
 * Copyright (C) 2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spray.json

import org.specs2.mutable._
import scala.util.control.NonFatal

class JsonParserSpecJvm extends Specification {

  "The JsonParser (on the JVM)" should {
    "be reentrant" in {
      import scala.concurrent.{ Await, Future }
      import scala.concurrent.duration._
      import scala.concurrent.ExecutionContext.Implicits.global

      val largeJsonSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/test.json")).mkString
      val list = Await.result(
        Future.traverse(List.fill(20)(largeJsonSource))(src => Future(src.parseJson)),
        5.seconds
      )
      list.map(_.asInstanceOf[JsObject].fields("questions").asInstanceOf[JsArray].elements.size) === List.fill(20)(100)
    }
    "fail gracefully for deeply nested structures" in {
      val stackSize = 128000

      def probe(depth: Int, maxDepth: Int): String = {
        val queue = new java.util.ArrayDeque[String]()

        val runnable = new Runnable {
          override def run(): Unit =
            try {
              val nested = "[{\"key\":" * (depth / 2)
              val settings = JsonParserSettings.default.withMaxDepth(maxDepth)
              nested.parseJson(settings)
              queue.push("didn't fail")
            } catch {
              case s: StackOverflowError => queue.push("stackoverflow")
              case NonFatal(e) =>
                queue.push(s"nonfatal: ${e.getMessage}")
            }
        }

        val thread = new Thread(null, runnable, "parser-test", stackSize)
        thread.start()
        thread.join()
        queue.peek
      }

      // Explicit type needed to compile on Scala 2.10, can be inlined later
      def inc(i: Int): Int = 1 + i
      val i: Int = Iterator.iterate(1)(inc).indexWhere(depth => probe(depth, maxDepth = 1000) contains "stackoverflow")
      println(s"Overflowing stack at $i which means we need about ${stackSize / i} bytes per recursive call")

      val maxDepth = i / 4 // should give lots of room
      probe(1500, maxDepth) ===
        s"nonfatal: JSON input nested too deeply:JSON input was nested more deeply than the configured limit of maxDepth = $maxDepth"
    }
  }

}
