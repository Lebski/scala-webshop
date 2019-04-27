package services

import java.util.UUID.randomUUID

import javax.inject._
import models.{Item, ShoppingCart, User}

import scala.collection.mutable


trait StoreService {
  def AddItem(price: Double, description: String): Item

  def GetItem(id: String): Item

  def UpdateItem(itemId: String, price: Double, description: String)

  def DeleteItem(itemId: String)

  def DecreaseStock(itemId: String, amount: Int)

  def IncreaseStock(itemId: String, amount: Int)

  def CalcPrice(cart: ShoppingCart): Double

  def Checkout(user: User): (Boolean, String)

  def WaresExist(id: String): Boolean

  def GetWares(): mutable.Map[String, Item]
}

@Singleton
class Store extends StoreService {
  private var wares = mutable.Map[String, Item]()

  @throws(classOf[Exception])
  override def GetItem(id: String): Item = {
    if (WaresExist(id)) {
      return wares(id)
    }
    throw new Exception("User not found")
  }

  @throws(classOf[Exception])
  override def UpdateItem(itemId: String, price: Double, description: String) {
    if (WaresExist(itemId)) {
      wares(itemId).description = description
      wares(itemId).price = price
    } else {
      throw new Exception("Item does not exist")
    }
  }

  @throws(classOf[Exception])
  override def AddItem(price: Double, description: String): Item = {
    val id = randomUUID().toString
    if (!WaresExist(id)) {
      val item: Item = new Item(id, price, description)
      wares += (item.id -> item)
      return item
    } else {
      throw new Exception("Item already exists")
    }
  }

  override def DeleteItem(itemId: String) {
    if (WaresExist(itemId)) {
      wares -= itemId
    }
  }


  override def DecreaseStock(itemId: String, amount: Int) {
    if (WaresExist(itemId)) {
      if (wares(itemId).stock - amount > 0) {
        wares(itemId).stock -= amount
      } else {
        throw new Exception("To few items in Stock")
      }
    } else {
      throw new Exception("Item does not exist")
    }
  }

  override def IncreaseStock(itemId: String, amount: Int) {
    if (WaresExist(itemId)) {
      wares(itemId).stock += amount
    } else {
      throw new Exception("Item does not exist")
    }
  }

  override def CalcPrice(cart: ShoppingCart): Double = {
    var price: Double = 0
    for (product <- cart.getCart()) {
      if (WaresExist(product._1)) {
        price += wares(product._1).price * product._2
      } else {
        throw new Exception("Item does not exist")
      }
    }
    return price
  }

  override def Checkout(user: User): (Boolean, String) = {
    val cart: ShoppingCart = user.shoppingCart
    var price: Double = 0
    var notInStock: List[String] = List.empty[String]
    var notValid: List[String] = List.empty[String]
    for (product <- cart.getCart()) {
      val itemId: String = product._1
      price += wares(product._1).price * product._2

      if (WaresExist(itemId)) {
        if (!(wares(itemId).stock - product._2 > 0)) {
          notInStock = notInStock :+ itemId
        }
      } else notValid = notValid :+ itemId
    }

    if ((notInStock.length != 0) || (notValid.length != 0)) {
      return (false, "Not in Stock: %s - Not valid: %s".format(notInStock mkString ", ", notValid mkString ", "))

    } else {
      for (product <- cart.getCart()) {
        val itemId: String = product._1
        wares(itemId).stock -= product._2
      }
      user.shoppingCart.resetCart()
      return (true, "Checkout successful")
    }

  }

  override def WaresExist(id: String): Boolean = {
    wares contains (id)
  }

  override def GetWares(): mutable.Map[String, Item] = {
    return wares
  }

}