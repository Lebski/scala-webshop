package models

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
