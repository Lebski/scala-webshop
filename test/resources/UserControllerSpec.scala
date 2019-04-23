import controllers.UserController

import scala.concurrent.Future
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class UserControllerSpec extends PlaySpec with Results {

  "GET#Users" should {
    "should be empty" in {
      val controller = new UserController(Helpers.stubControllerComponents())
      val result: Future[Result] = controller.index().apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "[]"
    }
  }

  }
}