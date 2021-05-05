package org.knoldus.jsonSupport

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.knoldus.models.{User, UserType}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

import java.util.UUID


  trait UserJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
    implicit def enumFormat[A <: Enumeration](implicit enu: A): RootJsonFormat[A#Value] = {
      new RootJsonFormat[A#Value] {
        def write(obj: A#Value): JsValue = JsString(obj.toString)

        def read(json: JsValue): A#Value = {
          json match {
            case JsString(txt) => enu.withName(txt)
            case somethingElse => throw DeserializationException(s"Expected a value from enum $enu instead of $somethingElse")
          }
        }
      }
    }
    implicit object UUIDFormat extends JsonFormat[UUID] {
      def write(uuid: UUID) = JsString(uuid.toString)
      def read(value: JsValue) = {
        value match {
          case JsString(uuid) => UUID.fromString(uuid)
          case _              => throw new DeserializationException("Expected hexadecimal UUID string")
        }
      }
    }
    implicit val userTypeJson: RootJsonFormat[UserType.Value] = enumFormat(UserType)
    implicit val userFormat: RootJsonFormat[User] = jsonFormat4(User)

  }




