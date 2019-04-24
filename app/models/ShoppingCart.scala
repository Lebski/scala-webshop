package models

import scala.collection.mutable

class ShoppingCart {

  private var shoppingCart = mutable.Map[String, Int]()


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
