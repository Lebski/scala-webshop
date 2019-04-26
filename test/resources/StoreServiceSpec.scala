import models.{Item, User}
import org.scalatestplus.play._
import services.Store

class StoreServiceSpec extends PlaySpec {
  "A StoreService" must {
    "add Items and Increase Stock" in {
      var store: Store = new Store
      store.AddItem(new Item("Chocolate", 1, "Abc"))
      store.AddItem(new Item("Coffee", 1, "Abc"))
      store.IncreaseStock("Chocolate", 2)
      store.GetItem("Chocolate").stock mustEqual 2
    }

    "decrease Stock" in {
      var store: Store = new Store
      store.AddItem(new Item("Chocolate", 1, "Abc"))
      store.AddItem(new Item("Coffee", 1, "Abc"))
      store.IncreaseStock("Chocolate", 2)
      store.GetItem("Chocolate").stock mustEqual 2
      store.DecreaseStock("Chocolate", 1)
      store.GetItem("Chocolate").stock mustEqual 1
    }


    "calc price of shoppinc Cart" in {
      var store: Store = new Store
      store.AddItem(new Item("Chocolate", 3.5, "Abc"))
      store.AddItem(new Item("Coffee", 2.5, "Abc"))
      store.IncreaseStock("Coffee", 2)
      store.IncreaseStock("Chocolate", 2)

      var user: User = new User("1", "1", "1", "de")
      user.shoppingCart.addCartItem("Chocolate", 1)
      user.shoppingCart.addCartItem("Coffee", 3)

      store.CalcPrice(user.shoppingCart) mustEqual 11
    }

    "checkout Shopping cart" in {
      var store: Store = new Store
      store.AddItem(new Item("Chocolate", 3.5, "Abc"))
      store.AddItem(new Item("Coffee", 2.5, "Abc"))
      store.IncreaseStock("Coffee", 2)
      store.IncreaseStock("Chocolate", 2)

      var user: User = new User("1", "1", "1", "de")
      user.shoppingCart.addCartItem("Chocolate", 1)
      user.shoppingCart.addCartItem("Coffee", 1)

      store.Checkout(user, user.shoppingCart)._1 mustBe true

      store.GetItem("Chocolate").stock mustBe 1
      store.GetItem("Coffee").stock mustBe 1

    }


    "not change if checkout is not successfull" in {
      var store: Store = new Store
      store.AddItem(new Item("Chocolate", 3.5, "Abc"))
      store.AddItem(new Item("Coffee", 2.5, "Abc"))
      store.IncreaseStock("Coffee", 2)
      store.IncreaseStock("Chocolate", 2)

      var user: User = new User("1", "1", "1", "de")
      user.shoppingCart.addCartItem("Chocolate", 1)
      user.shoppingCart.addCartItem("Coffee", 3)

      store.Checkout(user, user.shoppingCart)._1 mustBe false

      store.GetItem("Chocolate").stock mustBe 2
      store.GetItem("Coffee").stock mustBe 2

    }
  }
}