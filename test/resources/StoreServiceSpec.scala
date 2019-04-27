import models.User
import org.scalatestplus.play._
import services.Store
import models.Item

class StoreServiceSpec extends PlaySpec {
  "A StoreService" must {
    "add Items and Increase Stock" in {
      var store: Store = new Store
      var item0: Item = store.AddItem(3.5, "Chocolate")
      var item1: Item = store.AddItem(2.5, "Coffee")
      store.IncreaseStock(item0.id, 2)
      store.GetItem(item0.id).stock mustEqual 2
    }

    "decrease Stock" in {
      var store: Store = new Store
      var item0: Item = store.AddItem(3.5, "Chocolate")
      var item1: Item = store.AddItem(2.5, "Coffee")
      store.IncreaseStock(item0.id, 2)
      store.GetItem(item0.id).stock mustEqual 2
      store.DecreaseStock(item0.id, 1)
      store.GetItem(item0.id).stock mustEqual 1
    }


    "calc price of shoppinc Cart" in {
      var store: Store = new Store
      var item0: Item = store.AddItem(3.5, "Chocolate")
      var item1: Item = store.AddItem(2.5, "Coffee")
      store.IncreaseStock(item1.id, 2)
      store.IncreaseStock(item0.id, 2)

      var user: User = new User("1", "1", "1", "de")
      user.shoppingCart.addCartItem(item0.id, 1)
      user.shoppingCart.addCartItem(item1.id, 3)

      store.CalcPrice(user.shoppingCart) mustEqual 11
    }

    "checkout Shopping cart" in {
      var store: Store = new Store
      var item0: Item = store.AddItem(3.5, "Chocolate")
      var item1: Item = store.AddItem(2.5, "Coffee")
      store.IncreaseStock(item1.id, 2)
      store.IncreaseStock(item0.id, 2)

      var user: User = new User("1", "1", "1", "de")
      user.shoppingCart.addCartItem(item0.id, 1)
      user.shoppingCart.addCartItem(item1.id, 1)

      store.Checkout(user)._1 mustBe true

      store.GetItem(item0.id).stock mustBe 1
      store.GetItem(item1.id).stock mustBe 1

    }


    "not change if checkout is not successfull" in {
      var store: Store = new Store
      var item0: Item = store.AddItem(3.5, "Chocolate")
      var item1: Item = store.AddItem(2.5, "Coffee")
      store.IncreaseStock(item1.id, 2)
      store.IncreaseStock(item0.id, 2)

      var user: User = new User("1", "1", "1", "de")
      user.shoppingCart.addCartItem(item0.id, 1)
      user.shoppingCart.addCartItem(item1.id, 3)

      store.Checkout(user)._1 mustBe false

      store.GetItem(item0.id).stock mustBe 2
      store.GetItem(item1.id).stock mustBe 2

    }
  }
}