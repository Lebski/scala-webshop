package services

import java.util.UUID.randomUUID

import javax.inject._
import models.{Item, Order, ShoppingCart, User}

import scala.collection.mutable


trait OrderService {
  def AddOrder(user: User, items: mutable.Map[String, Int]): Order

  def GetOrder(id: String): Order

  def DeleteOrder(itemId: String)

  def GetAllOrders(): Iterable[Order]

  def OrderExists(id: String): Boolean
}

@Singleton
class Orders extends OrderService {
  private var orders = mutable.Map[String, Order]()

  @throws(classOf[Exception])
  override def AddOrder(user: User, items: mutable.Map[String, Int]): Order = {
    val id = randomUUID().toString
    if (!OrderExists(id)) {
      val order: Order = new Order(id, user, items)
      orders += (order.id -> order)
      return order
    } else {
      throw new Exception("Item already exists")
    }
  }

  @throws(classOf[Exception])
  override def GetOrder(id: String): Order = {
    if (OrderExists(id)) {
      return orders(id)
    }
    throw new Exception("User not found")
  }

  override def DeleteOrder(itemId: String) {
    if (OrderExists(itemId)) {
      orders -= itemId
    }
  }

  override def OrderExists(id: String): Boolean = {
    orders contains (id)
  }

  override def GetAllOrders(): Iterable[Order] = {
    return orders.values
  }

}