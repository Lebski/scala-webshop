package models

import java.time.temporal.TemporalAmount

import scala.collection.mutable

class User(var id: String, var credentials: Credentials, var firstName: String, var lastName: String, var language: String) {


 private var shoppingCart = mutable.Map[Int, Int]()

    var address: Address = _
    var bankAccount: BankAccount = _

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
