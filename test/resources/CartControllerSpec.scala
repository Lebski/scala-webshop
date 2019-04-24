import controllers.{CartController, UserController}
import models.User
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
  var users: Users = new Users
  val testController = new UserController(Helpers.stubControllerComponents(), users)
  val testRequest = FakeRequest(POST, "/").withJsonBody(Json.parse(
    """
      |{
      |	"password": "SECUREPASS",
      |	"email": "email@email.de",
      |	"firstName": "Felix",
      |	"lastName": "Leber",
      |	"language": "German"
      |}
    """.stripMargin))
  val testPostResultRaw: Future[Result] = testController.PostUsers.apply(testRequest)

  case class testPostResult(val userId: String, val message: String)

  implicit val testResultReads: Reads[testPostResult] = (
    (JsPath \ "userId").read[String](minLength[String](2)) and
      (JsPath \ "message").read[String](minLength[String](2))
    ) (testPostResult.apply _)
  val postResult: testPostResult = contentAsJson(testPostResultRaw).as(testResultReads)

  // Setup Controller
  val controller = new CartController(Helpers.stubControllerComponents(), users)


  "GET#Cart" should {
    "return an empty list" in {
      var user: User = users.getUser(postResult.userId)
      user.addCartItem("TestItem", 5)
      user.addCartItem("TestItem1", 10)
      val result: Future[Result] = controller.GetCart(postResult.userId).apply(FakeRequest())

      case class GetResult(val TestItem: Int, val TestItem1: Int)
      implicit val ResultReads: Reads[GetResult] = (
        (JsPath \ "TestItem").read[Int] and
          (JsPath \ "TestItem1").read[Int]
        ) (GetResult.apply _)

      val getResult: GetResult = contentAsJson(result).as(ResultReads)
      getResult.TestItem mustEqual 5
      getResult.TestItem1 mustEqual 10
      user.resetCart()
    }
  }

  "POST#Cart" should {
    "add items to shopping cart" in {
      var user: User = users.getUser(postResult.userId)
      //      user.addCartItem("TestItem", 5)
      //      user.addCartItem("TestItem1", 10)
      val postRequest = FakeRequest(POST, "/").withJsonBody(Json.parse(
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
      val result: Future[Result] = controller.UpdateCart(postResult.userId).apply(postRequest)
      user.getCart()("TestProduct") mustEqual 5
      user.resetCart()
    }
    "remove items from shopping cart" in {
      var user: User = users.getUser(postResult.userId)
      user.addCartItem("TestProduct", 5)
      user.addCartItem("TestProduct1", 1000)
      val postRequest = FakeRequest(POST, "/").withJsonBody(Json.parse(
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
      val result: Future[Result] = controller.UpdateCart(postResult.userId).apply(postRequest)
      user.getCart()("TestProduct") mustEqual 2
      user.getCart()("TestProduct1") mustEqual 500
      user.resetCart()
    }
    "remove an items completely from shopping cart" in {
      var user: User = users.getUser(postResult.userId)
      user.addCartItem("TestProduct", 5)
      user.addCartItem("TestProduct1", 5)
      val postRequest = FakeRequest(POST, "/").withJsonBody(Json.parse(
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
      val result: Future[Result] = controller.UpdateCart(postResult.userId).apply(postRequest)
      user.getCart() contains "TestProduct" mustBe false
      user.getCart() contains "TestProduct1" mustBe true
      user.resetCart()
    }

  }

  "DELETE#Cart" should {
    "remove all Items from cart" in {
      var user: User = users.getUser(postResult.userId)
      user.addCartItem("TestProduct", 5)
      user.addCartItem("TestProduct1", 5)
      val postRequest = FakeRequest(DELETE, "/")
      val result: Future[Result] = controller.ResetCart(postResult.userId).apply(postRequest)
      user.getCart() contains "TestProduct" mustBe false
      user.getCart() contains "TestProduct1" mustBe false
      user.resetCart()
    }
  }


}