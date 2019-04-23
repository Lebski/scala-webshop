import controllers.UserController

import scala.concurrent.Future
import org.scalatestplus.play._
import play.api.libs.json.Reads.minLength
import play.api.libs.json.{JsPath, JsValue, Json, Reads}
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import services.Users

class UserControllerSpec extends PlaySpec with Results {

  "GET#Users" should {
    "should be empty" in {
      val controller = new UserController(Helpers.stubControllerComponents(), new Users)
      val result: Future[Result] = controller.GetUsers.apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "[]"
    }
  }

  "GET#Users" should {
    "be valid" in {
      var users: Users = new Users
      val controller = new UserController(Helpers.stubControllerComponents(), users)
      val request = FakeRequest(POST, "/").withJsonBody(Json.parse(
        """
          |{
          |	"password": "SECUREPASS",
          |	"email": "email@email.de",
          |	"firstName": "Felix",
          |	"lastName": "Leber",
          |	"language": "Geman"
          |}
        """.stripMargin))
      val postResultRaw: Future[Result] = controller.PostUsers.apply(request)
      case class PostResult(val userId: String,val message: String)
      implicit val resultReads: Reads[PostResult] = (
        (JsPath \ "userId").read[String](minLength[String](2)) and
          (JsPath \ "message").read[String](minLength[String](2))
        )(PostResult.apply _)
      val postResult: PostResult = contentAsJson(postResultRaw).as(resultReads)

      users.getAllUserIds().exists(x => {x == postResult.userId})

    }
  }
}