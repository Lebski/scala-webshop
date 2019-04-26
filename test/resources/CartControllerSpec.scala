import controllers.{CartController, UserController}
import auth.{AuthAction, AuthService}
import javax.inject.Inject
import models.{ShoppingCart, User}
import org.scalatestplus.play._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import services.Users

import scala.concurrent.Future

class CartControllerSpec extends PlaySpec with Results {

  // Inical Test-Setup


  // Setup controller

  var users: Users = new Users
  var authService: AuthService = new AuthService(users)


  val controllerComponents = Helpers.stubControllerComponents()
  val authAction = new AuthAction(controllerComponents.parsers.default, authService)(controllerComponents.executionContext)
  val controller = new CartController(Helpers.stubControllerComponents(), users, authAction)

  // Setup admin account

  val (_, adminId) = users.addAdmin("test", "test", "test", "test", "test")
  var (_, token) = authService.validateUser(adminId, "test")

  val testRequest =  FakeRequest().withHeaders(AUTHORIZATION -> token)

  // Setup inital User

  val userId = users.addNewuser("testUser", "testUser@gmailcom", "test", "test", "test")
  var (_, userToken) = authService.validateUser(adminId, "test")


  "GET#Cart" should {
    "return a Cart with Items" in {
      var user: User = users.getUser(userId)
      user.shoppingCart.addCartItem("TestItem", 5)
      user.shoppingCart.addCartItem("TestItem1", 10)
      val result: Future[Result] = controller.GetCart(userId).apply(testRequest)

      case class GetResult(val TestItem: Int, val TestItem1: Int)
      implicit val ResultReads: Reads[GetResult] = (
        (JsPath \ "TestItem").read[Int] and
          (JsPath \ "TestItem1").read[Int]
        ) (GetResult.apply _)

      val getResult: GetResult = contentAsJson(result).as(ResultReads)
      getResult.TestItem mustEqual 5
      getResult.TestItem1 mustEqual 10
      user.shoppingCart.resetCart()
    }

    "return a Cart with Items with User credentials" in {
      var user: User = users.getUser(userId)
      user.shoppingCart.addCartItem("TestItem", 5)
      user.shoppingCart.addCartItem("TestItem1", 10)
      val result: Future[Result] = controller.GetCart(userId).apply(FakeRequest().withHeaders(AUTHORIZATION -> userToken))

      case class GetResult(val TestItem: Int, val TestItem1: Int)
      implicit val ResultReads: Reads[GetResult] = (
        (JsPath \ "TestItem").read[Int] and
          (JsPath \ "TestItem1").read[Int]
        ) (GetResult.apply _)

      val getResult: GetResult = contentAsJson(result).as(ResultReads)
      getResult.TestItem mustEqual 5
      getResult.TestItem1 mustEqual 10
      user.shoppingCart.resetCart()
    }
  }

  "POST#Cart" should {
    "add items to shopping cart" in {
      var user: User = users.getUser(userId)
      //      user.addCartItem("TestItem", 5)
      //      user.addCartItem("TestItem1", 10)
      val postRequest = FakeRequest(POST, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
        """
          |{
          |	"info": "test",
          |	"updates": [{
          |		"productId": "TestProduct",
          |		"updateOperation": "add",
          |		"quantity": 5
          |	}]
          |}
        """.stripMargin))
      val result: Future[Result] = controller.UpdateCart(userId).apply(postRequest)
      user.shoppingCart.getCart()("TestProduct") mustEqual 5
      user.shoppingCart.resetCart()
    }
    "remove items from shopping cart" in {
      var user: User = users.getUser(userId)
      user.shoppingCart.addCartItem("TestProduct", 5)
      user.shoppingCart.addCartItem("TestProduct1", 1000)
      val postRequest = FakeRequest(POST, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
        """
          |{
          |	"info": "test",
          |	"updates": [{
          |		"productId": "TestProduct",
          |		"updateOperation": "remove",
          |		"quantity": 3
          |	},
          | {
          |		"productId": "TestProduct1",
          |		"updateOperation": "remove",
          |		"quantity": 500
          |	}]
          |}
        """.stripMargin))
      val result: Future[Result] = controller.UpdateCart(userId).apply(postRequest)
      user.shoppingCart.getCart()("TestProduct") mustEqual 2
      user.shoppingCart.getCart()("TestProduct1") mustEqual 500
      user.shoppingCart.resetCart()
    }
    "remove an items completely from shopping cart" in {
      var user: User = users.getUser(userId)
      user.shoppingCart.addCartItem("TestProduct", 5)
      user.shoppingCart.addCartItem("TestProduct1", 5)
      val postRequest = FakeRequest(POST, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
        """
          |{
          |	"info": "test",
          |	"updates": [{
          |		"productId": "TestProduct",
          |		"updateOperation": "discard",
          |		"quantity": 0
          |	}]
          |}
        """.stripMargin))
      val result: Future[Result] = controller.UpdateCart(userId).apply(postRequest)
      user.shoppingCart.getCart() contains "TestProduct" mustBe false
      user.shoppingCart.getCart() contains "TestProduct1" mustBe true
      user.shoppingCart.resetCart()
    }

  }

  "DELETE#Cart" should {
    "remove all Items from cart" in {
      var user: User = users.getUser(userId)
      user.shoppingCart.addCartItem("TestProduct", 5)
      user.shoppingCart.addCartItem("TestProduct1", 5)
      val postRequest = FakeRequest(DELETE, "/").withHeaders(AUTHORIZATION -> token)
      val result: Future[Result] = controller.ResetCart(userId).apply(postRequest)
      user.shoppingCart.getCart() contains "TestProduct" mustBe false
      user.shoppingCart.getCart() contains "TestProduct1" mustBe false
      user.shoppingCart.resetCart()
    }
  }


}