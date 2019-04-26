import auth.{AuthAction, AuthService}
import controllers.UserController
import javax.inject.Inject
import org.scalatestplus.play._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.mvc._
import play.api.mvc.BodyParsers
import play.api.test.Helpers._
import play.api.test._
import services.Users
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._


import scala.concurrent.{ExecutionContext, Future}

class UserControllerSpec extends PlaySpec with Results {

  // Setup controller

  var users: Users = new Users
  var authService: AuthService = new AuthService(users)


  val controllerComponents = Helpers.stubControllerComponents()
  val authAction = new AuthAction(controllerComponents.parsers.default, authService)(controllerComponents.executionContext)
  val controller = new UserController(Helpers.stubControllerComponents(), users, authAction)

  // Setup admin account

  val (_, adminId) = users.addAdmin("test", "test", "test", "test", "test")
  var (_, token) = authService.validateUser(adminId, "test")

  val testRequest =  FakeRequest().withHeaders(AUTHORIZATION -> token)



  "GET#Users" should {
    "return an empty list" in {
      val result: Future[Result] = controller.GetUsers.apply(testRequest)
      status(result) mustEqual 200
    }
  }

  "POST#Users" should {
    "add a user to the users service" in {
      val request = FakeRequest(POST, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
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
      }) mustEqual true

    }
  }

  "GET#User" should {
    "return a single user" in {
      val request = FakeRequest(POST, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
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

      val result: Future[Result] = controller.GetUser(postResult.userId).apply(testRequest)
      status(result) mustEqual OK

    }
  }

  "PUT#User" should {
    "modify a single user" in {
      val request = FakeRequest(POST, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
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

      val updateRequest = FakeRequest(PUT, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
        """
          |{
          |	"firstName": "Daniel"
          |}
        """.stripMargin))

      val result: Future[Result] = controller.UpdateUser(postResult.userId).apply(updateRequest)
      status(result) mustEqual OK

      users.userExists(postResult.userId) mustEqual true
      users.getUser(postResult.userId).firstName mustEqual "Daniel"


    }
  }

  "DELTE#User" should {
    "delte a single user" in {
      val request = FakeRequest(POST, "/").withHeaders(AUTHORIZATION -> token).withJsonBody(Json.parse(
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

      val updateRequest = FakeRequest(DELETE, "/").withHeaders(AUTHORIZATION -> token)
      val result: Future[Result] = controller.DeleteUser(postResult.userId).apply(updateRequest)
      status(result) mustEqual OK

      users.userExists(postResult.userId) mustEqual false
    }
  }

}