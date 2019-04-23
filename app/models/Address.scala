package models

import java.util.Locale.IsoCountryCode

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class Address(countryCode: String, state: String, city: String, street: String, number: Int) {


}
object Address {
  implicit val addressWrites: Writes[Address] = (
    (JsPath \ "countryCode").write[String] and
      (JsPath \ "state").write[String] and
      (JsPath \ "city").write[String] and
      (JsPath \ "street").write[String] and
      (JsPath \ "number").write[Int]
    )(unlift(Address.unapply))
}
