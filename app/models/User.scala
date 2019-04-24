package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json.{JsPath, Reads, Writes}

import scala.collection.mutable


case
class User(var id: String, var firstName: String, var lastName: String, var language: String) {


  private var shoppingCart = mutable.Map[String, Int]()

  var address: Address = new Address("none", "none", "none", "none", 0)
  var bankAccount: BankAccount = new BankAccount("none", "none", "none")
  var credentials: Credentials = _

  val active: Boolean = true

  def getCart(): mutable.Map[String, Int] = shoppingCart

  def cartItems(): Iterable[String] = shoppingCart.keys

  def addCartItem(itemId: String, amount: Int) {
    if (shoppingCart contains itemId) {
      shoppingCart(itemId) += amount
    } else {
      shoppingCart += (itemId -> amount)
    }
  }


  def removeCartItem(itemId: String, amount: Int) {
    if (shoppingCart contains itemId) {
      if (shoppingCart(itemId) - amount > 0) {
        shoppingCart(itemId) -= amount
      } else {
        shoppingCart -= itemId
      }
    }
  }

  def discardCartItem(itemId: String) {
    if (shoppingCart contains itemId) {
      shoppingCart -= itemId
    }
  }

  def resetCart() {
    if (shoppingCart.nonEmpty) {
      shoppingCart = mutable.Map[String, Int]()
    }
  }

}

object User {
  implicit val userWrites: Writes[User] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "firstName").write[String] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "language").write[String]
    ) (unlift(User.unapply))


  implicit val userReads: Reads[User] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "firstName").read[String](minLength[String](2)) and
      (JsPath \ "lastName").read[String](minLength[String](2)) and
      (JsPath \ "language").read[String](minLength[String](2))
    ) (User.apply _)
}