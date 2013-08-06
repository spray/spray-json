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

import org.specs2.mutable.Specification
import java.util.NoSuchElementException

/**
 * User: cvrabie
 * Date: 05/08/2013
 */
class RichJsObjectSpec extends Specification with DefaultJsonProtocol{

  object Currency extends Enumeration{
    val GBP = Value("GBP")
    val USD = Value("USD")
    //we could write a custom reader/format but it's here to show RichJsObjectSpec.field(fieldName, func)
    def fromJson(json:JsValue) = json match {
      case JsString(str) => try{ withName(str) }catch{
        case e: NoSuchElementException => throw new DeserializationException("No currency "+str)
      }
      case x => throw new DeserializationException("Expecting a Currency as string but got "+x)
    }
  }
  case class User(name: String, age: Int, plan:BillingPlan, language:String = "en_GB")
  case class BillingPlan(name: String, cost: Double, currency:Currency.Value = Currency.GBP)

  implicit val PlanReader = new JsonReader[BillingPlan]{
    import RichJsObject._
    def read(json: JsValue) = readRich(json)
    def readRich(obj: RichJsObject) = (for{
      name <- obj.string("name").right
      cost <- obj.double("cost").right
      currency <- obj.field("currency",Currency.fromJson _,Currency.GBP).right
    }yield BillingPlan(name, cost, currency)) match{
      case Right(billingPlan) => billingPlan
      case Left(error) => throw new DeserializationException(error)
    }
  }

  implicit val UserReader = new JsonReader[User] {
    import RichJsObject._
    def read(json: JsValue) = readRich(json)
    def readRich(obj: RichJsObject) = (for{
      name <- obj.string("name").right
      age <- obj.int("age").right
      plan <- obj.t("plan").right
      language <- obj.string("language","en_GB").right
    }yield User(name, age, plan, language)) match{
      case Right(user) => user
      case Left(error) => throw new DeserializationException(error)
    }
  }

  "A custom JsonReader" should {
    
    "correctly deserialize a valid json" in {
      val json =  """{"name":"Bob","age":24,"plan":{"name":"free","cost":12.34,"currency":"USD"},"language":"en_US"}""".asJson
      val expected = User("Bob",24,BillingPlan("free",12.34,Currency.USD),"en_US")
      json.convertTo[User] must_==(expected)
    }

    "provide defaults of optional json fields" in {
      val json =  """{"name":"Bob","age":24,"plan":{"name":"free","cost":12.34}}""".asJson
      val expected = User("Bob",24,BillingPlan("free",12.34,Currency.GBP),"en_GB")
      json.convertTo[User] must_==(expected)
    }

    "throw an exception if a required field is missing" in {
      val json =  """{"age":24,"plan":{"name":"free","cost":12.34}}""".asJson
      json.convertTo[User] must throwA(new DeserializationException("Field 'name' is missing"))
    }

    "throw an exception if a field is not of the expected type" in{
      val json =  """{"name":"Bob","age":"abc","plan":{"name":"free","cost":12.34}}""".asJson 
      json.convertTo[User] must throwA(new DeserializationException("Expecting JsNumber in field 'age' but got \"abc\""))
    }

    "throw an exception if a lossy conversion is done in a field" in{
      val json =  """{"name":"Bob","age":999999999999999,"plan":{"name":"free","cost":12.34}}""".asJson 
      json.convertTo[User] must throwA(new DeserializationException("The value in field 'age' is too big to fit an Int"))
    }
  }

}
