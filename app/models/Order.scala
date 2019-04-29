package models

import java.util.{Calendar, Date}

import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.{JsPath, Writes}

import scala.collection.mutable

case class Order(id: String, user: User, items: mutable.Map[String, Int]) {
  val timestamp: Date = Calendar.getInstance.getTime

}

object Order {
  implicit val orderWrites: Writes[Order] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "user").write[User] and
      (JsPath \ "items").write[mutable.Map[String, Int]]
    ) (unlift(Order.unapply))
}
