package controllers

import auth.AuthAction
import javax.inject._
import models.{Address, BankAccount, Credentials, User}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.minLength
import play.api.libs.json._
import play.api.mvc.{Results, _}
import services.Users

/**
  * Define CRUD-Actions on the [[Users]] singleton object.
  * Uses the [[AuthAction]] authentication.
  *
  * @param cc    standard controller components
  * @param users All collection of all users in the system
  */
@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               users: Users,
                               authAction: AuthAction) extends AbstractController(cc) {

  // Add classes for JWT parsing

  case class PostUser(var password: String, var email: String, var firstName: String, var lastName: String, var language: String)

  implicit val postUserReads: Reads[PostUser] = (
    (JsPath \ "password").read[String](minLength[String](2)) and
      (JsPath \ "email").read[String](Reads.email) and
      (JsPath \ "firstName").read[String](minLength[String](2)) and
      (JsPath \ "lastName").read[String](minLength[String](2)) and
      (JsPath \ "language").read[String](minLength[String](2))
    ) (PostUser.apply _)
  implicit val postUserWrites: Writes[PostUser] = (
    (JsPath \ "password").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "firstName").write[String] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "language").write[String]
    ) (unlift(PostUser.unapply))


  case class AggregatedUser(val id: String, val user: User, val address: Address, val bankAccount: BankAccount, val mail: String)

  implicit val responseUserReads: Reads[AggregatedUser] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "user").read[User] and
      (JsPath \ "address").read[Address] and
      (JsPath \ "bankAccount").read[BankAccount] and
      (JsPath \ "email").read[String]
    ) (AggregatedUser.apply _)

  implicit val responseUserWrites: Writes[AggregatedUser] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "user").write[User] and
      (JsPath \ "address").write[Address] and
      (JsPath \ "bankAccount").write[BankAccount] and
      (JsPath \ "email").write[String]
    ) (unlift(AggregatedUser.unapply))

  case class UpdateUser(val password: Option[String], val email: Option[String], var firstName: Option[String], var lastName: Option[String], var language: Option[String])

  implicit val updateUserReads: Reads[UpdateUser] = (
    (JsPath \ "password").readNullable[String](minLength[String](2)) and
      (JsPath \ "email").readNullable[String](Reads.email) and
      (JsPath \ "firstName").readNullable[String](minLength[String](2)) and
      (JsPath \ "lastName").readNullable[String](minLength[String](2)) and
      (JsPath \ "language").readNullable[String](minLength[String](2))
    ) (UpdateUser.apply _)

  /**
    * Return all users
    * GET /users
    */
  def GetUsers = authAction { request =>
    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    Ok(Json.toJson(users.getAllUsers()))
  }

  /**
    * Create a new user
    * POST /users
    */
  def PostUsers = authAction { request =>
    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      val json = request.body.asJson.get
      val GeneratedUser = json.as[PostUser]

      val userId: String = users.addNewuser(GeneratedUser.password, GeneratedUser.email, GeneratedUser.firstName, GeneratedUser.lastName, GeneratedUser.language)

      val response: String =
        """
          |{
          | "userId": "%s",
          | "message": "User created"
          |}
        """.stripMargin

      Ok(response.format(userId))
    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        InternalServerError("Something went wrong")
    }
  }

  /**
    * Return a specific user
    * GET /users/#userid
    */
  def GetUser(userId: String) = authAction { request =>

    if (users.isAdminOrOwner(userId, request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      val user: User = users.getUser(userId)
      val aggregatedUser: AggregatedUser = new AggregatedUser(user.id, user, user.address, user.bankAccount, user.credentials.mail)
      Ok(Json.toJson(aggregatedUser))

    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

  /**
    * Edit fields of user
    * PUT /users/#userid
    */
  def UpdateUser(userId: String) = authAction { request =>

    if (users.isAdminOrOwner(userId, request.userId)) Results.Unauthorized("Authorized user not valid for this action")


    val json = request.body.asJson.get
    try {
      val updatedUser = json.as[UpdateUser]

      val user: User = users.getUser(userId)

      if (updatedUser.email.isDefined) user.credentials.mail = updatedUser.email.get
      if (updatedUser.firstName != None) user.firstName = updatedUser.firstName.get
      if (updatedUser.lastName != None) user.lastName = updatedUser.lastName.get
      if (updatedUser.language != None) user.language = updatedUser.language.get
      if (updatedUser.password != None) user.credentials = new Credentials(user.credentials.mail, updatedUser.password.get)

      val aggregatedUser: AggregatedUser = new AggregatedUser(user.id, user, user.address, user.bankAccount, user.credentials.mail)

      val response: String =
        """
          |{
          | "user": %s,
          | "message": "User updated"
          |}
        """.stripMargin

      Ok(response.format(Json.toJson(aggregatedUser)))

    } catch {
      case e: JsResultException => println(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

  /**
    * Delete user
    * DELETE /users/#userid
    */
  def DeleteUser(userId: String) = authAction { request =>

    if (users.isAdminOrOwner(userId, request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      var msg: String = ""
      if (users.userExists(userId)) {
        users.deleteUser(userId)
        msg = "User deleted"
      } else {
        msg = "User does not exist"
      }
      val response: String =
        """
          |{
          | "user": "%s",
          | "message": "%s"
          |}
        """.stripMargin

      Ok(response.format(userId, msg))

    } catch {
      case e: Throwable => println(e)
        InternalServerError("Something went wrong")
    }
  }

}



