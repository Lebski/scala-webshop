import models.{User}
import org.scalatestplus.play._
import auth.AuthService
import services.Users

class AuthServiceSpec extends PlaySpec {

  var users: Users = new Users
  val authService: AuthService = new AuthService(users)
  val testUserId = users.addNewuser("supesrsupersecret", "test", "test", "test", "test")


  "A AuthService" must {
    "create a JWT" in {
      authService.createToken("userid")
    }

    "return a JWT if user is valid" in {
      val (success, token) = authService.validateUser(testUserId, "supesrsupersecret")
      success mustEqual true
    }

    "return no JWT if user is not valid" in {
      val (success, token) = authService.validateUser(testUserId, "wrongPassword")
      success mustEqual false

    }

    "validate a JWT" in {
      val jwt: String = authService.createToken("userid")
      val res = authService.validateToken(jwt)
      res mustBe true
    }

    "un-validate a not valid JWT" in {
      // jwt was singed with a different secretkey
      val jwt: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJTY2FsYVdlYlNob3AiLCJzdWIiOiJ1c2VyaWQiLCJleHAiOjE1NTYxODE0Mjg3ODJ9.LsvVhYiqEGAkN7v7esYY1oWCcf54Ap6MD5xfHYMDsuk"
      val res = authService.validateToken(jwt)
      res mustBe false
    }

    "parse a JWT" in {
      val jwt: String = authService.createToken("userid")
      val res = authService.validateToken(jwt)
      res mustBe true
      val (success, userId) = authService.parsingCredentials(jwt)
      println(success, userId)
    }
  }
}