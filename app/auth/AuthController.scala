package controllers

import auth.AuthService
import javax.inject._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json._
import play.api.mvc._
import services.Users

/**
  * Define CRUD-Actions on the [[Users]] singleton object.
  *
  * @param cc    standard controller components
  * @param users All collection of all users in the system
  */
@Singleton
class AuthController @Inject()(cc: ControllerComponents,
                               users: Users,
                               auth: AuthService) extends AbstractController(cc) {

  // Add classes for JWT parsing

  case class TokenClaim(userId: String, password: String)

  implicit val postTokenReads: Reads[TokenClaim] = (
    (JsPath \ "userId").read[String](minLength[String](2)) and
      (JsPath \ "password").read[String](minLength[String](2))
    ) (TokenClaim.apply _)

  case class PostAdmin(var password: String, var email: String, var firstName: String, var lastName: String, var language: String)

  implicit val postAdminReads: Reads[PostAdmin] = (
    (JsPath \ "password").read[String](minLength[String](2)) and
      (JsPath \ "email").read[String](Reads.email) and
      (JsPath \ "firstName").read[String](minLength[String](2)) and
      (JsPath \ "lastName").read[String](minLength[String](2)) and
      (JsPath \ "language").read[String](minLength[String](2))
    ) (PostAdmin.apply _)

  def ClaimToken = Action { request =>
    try {
      val json = request.body.asJson.get
      val claim = json.as[TokenClaim]

      val (success, token) = auth.validateUser(claim.userId, claim.password)

      var response: String = ""
      if (success) {

        response =
          """
            |{
            | "JWT": "%s",
            | "status": "Successful"
            |}
          """.stripMargin

      } else {
        response =
          """
            |{
            | "JWT": "%s",
            | "status": "No success"
            |}
          """.stripMargin
      }

      Ok(response.format(token))

    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        InternalServerError("Something went wrong")
    }
  }

  /**
    * Create the admin account
    * POST /auth/admin
    */
  def AddAdmin = Action { request =>

    try {
      val json = request.body.asJson.get
      val GeneratedUser = json.as[PostAdmin]

      val (success, userId) = users.addAdmin(GeneratedUser.password, GeneratedUser.email, GeneratedUser.firstName, GeneratedUser.lastName, GeneratedUser.language)

      var message: String = ""

      if (success) message = "Admin created"
      else message = "Could not create Admin. Admin exists already."

      val response: String =
        """
          |{
          | "userId": "%s",
          | "message": "%s",
          | "success": %b
          |}
        """.stripMargin

      Ok(response.format(userId, message, success))
    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        InternalServerError("Something went wrong")
    }
  }
}