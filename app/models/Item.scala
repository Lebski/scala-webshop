package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json.{JsPath, Reads, Writes}

case class Item(id: String, var price: Double, var description: String) {

  var stock = 0

  def fill(amount: Int) {
    stock += amount
  }

  def book(amount: Int): Boolean = {
    if (stock >= amount) {
      stock -= amount
      return true
    }
    return false
  }
}

object Item {
  implicit val itemWrites: Writes[Item] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "price").write[Double] and
      (JsPath \ "description").write[String]
    ) (unlift(Item.unapply))


  implicit val itemReads: Reads[Item] = (
    (JsPath \ "id").read[String](minLength[String](2)) and
      (JsPath \ "price").read[Double] and
      (JsPath \ "description").read[String](minLength[String](2))
    ) (Item.apply _)
}