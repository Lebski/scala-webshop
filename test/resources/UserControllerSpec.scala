import controllers.UserController
import org.scalatestplus.play._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import services.Users

import scala.concurrent.Future

class UserControllerSpec extends PlaySpec with Results {

  "GET#Users" should {
    "should return an empty list" in {
      val controller = new UserController(Helpers.stubControllerComponents(), new Users)
      val result: Future[Result] = controller.GetUsers.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "[]"
    }
  }

  "POST#Users" should {
    "should add a user to the users service" in {
      var users: Users = new Users
      val controller = new UserController(Helpers.stubControllerComponents(), users)
      val request = FakeRequest(POST, "/").withJsonBody(Json.parse(
        """
          |{
          |	"password": "SECUREPASS",
          |	"email": "email@email.de",
          |	"firstName": "Felix",
          |	"lastName": "Leber",
          |	"language": "German"
          |}
        """.stripMargin))
      val postResultRaw: Future[Result] = controller.PostUsers.apply(request)
      case class PostResult(val userId: String, val message: String)
      implicit val resultReads: Reads[PostResult] = (
        (JsPath \ "userId").read[String](minLength[String](2)) and
          (JsPath \ "message").read[String](minLength[String](2))
        ) (PostResult.apply _)
      val postResult: PostResult = contentAsJson(postResultRaw).as(resultReads)

      users.getAllUserIds().exists(x => {
        x == postResult.userId
      })

    }
  }

  "GET#User" should {
    "should return a single user" in {
      var users: Users = new Users
      val controller = new UserController(Helpers.stubControllerComponents(), users)
      val request = FakeRequest(POST, "/").withJsonBody(Json.parse(
        """
          |{
          |	"password": "SECUREPASS",
          |	"email": "email@email.de",
          |	"firstName": "Felix",
          |	"lastName": "Leber",
          |	"language": "German"
          |}
        """.stripMargin))
      val postResultRaw: Future[Result] = controller.PostUsers.apply(request)
      case class PostResult(val userId: String, val message: String)
      implicit val resultReads: Reads[PostResult] = (
        (JsPath \ "userId").read[String](minLength[String](2)) and
          (JsPath \ "message").read[String](minLength[String](2))
        ) (PostResult.apply _)
      val postResult: PostResult = contentAsJson(postResultRaw).as(resultReads)

      val result: Future[Result] = controller.GetUser(postResult.userId).apply(FakeRequest())
      status(result) mustEqual OK

    }
  }

  "PUT#User" should {
    "should modify a single user" in {
      var users: Users = new Users
      val controller = new UserController(Helpers.stubControllerComponents(), users)
      val request = FakeRequest(POST, "/").withJsonBody(Json.parse(
        """
          |{
          |	"password": "SECUREPASS",
          |	"email": "email@email.de",
          |	"firstName": "Felix",
          |	"lastName": "Leber",
          |	"language": "German"
          |}
        """.stripMargin))
      val postResultRaw: Future[Result] = controller.PostUsers.apply(request)
      case class PostResult(val userId: String, val message: String)
      implicit val resultReads: Reads[PostResult] = (
        (JsPath \ "userId").read[String](minLength[String](2)) and
          (JsPath \ "message").read[String](minLength[String](2))
        ) (PostResult.apply _)
      val postResult: PostResult = contentAsJson(postResultRaw).as(resultReads)

      val updateRequest = FakeRequest(PUT, "/").withJsonBody(Json.parse(
        """
          |{
          |	"firstName": "Daniel"
          |}
        """.stripMargin))

      val result: Future[Result] = controller.UpdateUser(postResult.userId).apply(updateRequest)
      status(result) mustEqual OK

      users.getUser(postResult.userId).firstName mustEqual "Daniel"

    }
  }

  "DELTE#User" should {
    "delte a single user" in {
      var users: Users = new Users
      val controller = new UserController(Helpers.stubControllerComponents(), users)
      val request = FakeRequest(POST, "/").withJsonBody(Json.parse(
        """
          |{
          |	"password": "SECUREPASS",
          |	"email": "email@email.de",
          |	"firstName": "Felix",
          |	"lastName": "Leber",
          |	"language": "German"
          |}
        """.stripMargin))
      val postResultRaw: Future[Result] = controller.PostUsers.apply(request)
      case class PostResult(val userId: String, val message: String)
      implicit val resultReads: Reads[PostResult] = (
        (JsPath \ "userId").read[String](minLength[String](2)) and
          (JsPath \ "message").read[String](minLength[String](2))
        ) (PostResult.apply _)
      val postResult: PostResult = contentAsJson(postResultRaw).as(resultReads)

      users.userExists(postResult.userId) mustEqual true

      val updateRequest = FakeRequest(DELETE, "/")
      val result: Future[Result] = controller.DeleteUser(postResult.userId).apply(updateRequest)
      status(result) mustEqual OK

      users.userExists(postResult.userId) mustEqual false


    }
  }

}