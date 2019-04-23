package controllers

import javax.inject._
import models.User
import play.api.mvc._
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.json.Reads.minLength
import services.Users
import play.api.libs.functional.syntax._

/**
  * Define CRUD-Actions on the [[Users]] singleton object.
  *
  * @param cc standard controller components
  * @param users All collection of all users in the system
  */
@Singleton
class UserController @Inject() (cc: ControllerComponents,
                                users: Users) extends AbstractController(cc) {


  /* Todo:
  ** Users **
  * Create > Post 200/500
  * ~Read~ > Get 200/500
  *
  * * Users/Id **
  * Read    > Get
  * Update  > Put
  * Delte   > Delete
  *
  ** Shopping Cart **
  * Add > Post
  * Remove > Post
  * Empty > Delete
  *
  ** Credentials **
  * Update > Put
  *
  ** Adress **
  * Update > Put
   */

  /**
    * Return all Users
    * GET /users
    */
  def GetUsers = Action {
    Ok(Json.toJson(users.getAllUsers()))
  }

  def PostUsers = Action { request =>
      case class PostUser(var password: String, var email: String, var firstName: String, var lastName: String, var language: String)
    implicit val userReads: Reads[PostUser] = (
        (JsPath \ "password").read[String](minLength[String](2)) and
        (JsPath \ "email").read[String](Reads.email) and
        (JsPath \ "firstName").read[String](minLength[String](2)) and
        (JsPath \ "lastName").read[String](minLength[String](2)) and
        (JsPath \ "language").read[String](minLength[String](2))
      )(PostUser.apply _)

      val json = request.body.asJson.get
      val GeneratedUser = json.as[PostUser]

      val userId:String = users.addNewuser(GeneratedUser.password, GeneratedUser.email, GeneratedUser.firstName, GeneratedUser.lastName, GeneratedUser.language)

      val response: String =
        """
          |{
          | "userI  d": "%s",
          | "message": "User created"
          |}
        """.stripMargin

      Ok(response.format(userId))
  }



}

