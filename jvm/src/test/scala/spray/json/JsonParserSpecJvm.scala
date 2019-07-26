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
      import scala.concurrent.{Await, Future}
      import scala.concurrent.duration._
      import scala.concurrent.ExecutionContext.Implicits.global

      val largeJsonSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/test.json")).mkString
      val list = Await.result(
        Future.traverse(List.fill(20)(largeJsonSource))(src => Future(JsonParser(src))),
        5.seconds
      )
      list.map(_.asInstanceOf[JsObject].fields("questions").asInstanceOf[JsArray].elements.size) === List.fill(20)(100)
    }
    "fail gracefully for deeply nested structures" in {
      val queue = new java.util.ArrayDeque[String]()

      // testing revealed that each recursion will need approx. 280 bytes of stack space
      val depth = 1500
      val runnable = new Runnable {
        override def run(): Unit =
          try {
            val nested = "[{\"key\":" * (depth / 2)
            JsonParser(nested)
            queue.push("didn't fail")
          } catch {
            case s: StackOverflowError => queue.push("stackoverflow")
            case NonFatal(e) =>
              queue.push(s"nonfatal: ${e.getMessage}")
          }
      }

      val thread = new Thread(null, runnable, "parser-test", 655360)
      thread.start()
      thread.join()
      queue.peek() === "nonfatal: JSON input nested too deeply:JSON input was nested more deeply than the configured limit of maxNesting = 1000"
    }
  }

}
