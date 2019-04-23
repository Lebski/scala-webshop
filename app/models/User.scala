package models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

import scala.collection.mutable


case
class User(var id: String, var firstName: String, var lastName: String, var language: String) {


 private var shoppingCart = mutable.Map[Int, Int]()

    var address: Address = _
    var bankAccount: BankAccount = _
    var credentials: Credentials = _

    val active: Boolean = true

    def getCart(): mutable.Map[Int, Int] = shoppingCart

    def cartItems(): Iterable[Int] = shoppingCart.keys

    def addCartItem(itemId: Int, amount: Int){
      if (shoppingCart contains itemId){
        shoppingCart(itemId) += amount
      } else {
        shoppingCart += (itemId -> amount)
      }
    }

    def removeCartItem(itemId: Int, amount: Int){
      if (shoppingCart contains itemId) {
        shoppingCart(itemId) -= amount
      }
    }

}

object User {
  implicit val userWrites: Writes[User] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "firstName").write[String] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "language").write[String]
    )(unlift(User.unapply))

}