package controllers

import javax.inject._
import models.{Address, BankAccount, Credentials, User}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json._
import play.api.mvc._
import services.Users
import auth.AuthService

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

  case class TokenClaim(userId: String, password: String)
  implicit val postUserReads: Reads[TokenClaim] = (
    (JsPath \ "userId").read[String](minLength[String](2)) and
    (JsPath \ "password").read[String](minLength[String](2))
    ) (TokenClaim.apply _)

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
}