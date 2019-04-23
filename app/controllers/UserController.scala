package controllers

import javax.inject._
import models.{Address, BankAccount, Credentials, User}
import play.api.mvc._
import play.api.libs.json.{JsPath, JsResultException, Json, Reads, Writes}
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

  case class PostUser(var password: String, var email: String, var firstName: String, var lastName: String, var language: String)
  implicit val postUserReads: Reads[PostUser] = (
    (JsPath \ "password").read[String](minLength[String](2)) and
      (JsPath \ "email").read[String](Reads.email) and
      (JsPath \ "firstName").read[String](minLength[String](2)) and
      (JsPath \ "lastName").read[String](minLength[String](2)) and
      (JsPath \ "language").read[String](minLength[String](2))
    )(PostUser.apply _)
  implicit val postUserWrites: Writes[PostUser] = (
    (JsPath \ "password").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "firstName").write[String] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "language").write[String]
    )(unlift(PostUser.unapply))


  case class AggregatedUser(val id: String, val user: User, val address: Address, val bankAccount: BankAccount, val mail: String)
  implicit val responseUserReads: Reads[AggregatedUser] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "user").read[User] and
    (JsPath \ "address").read[Address] and
    (JsPath \ "bankAccount").read[BankAccount] and
    (JsPath \ "email").read[String]
    )(AggregatedUser.apply _)

  implicit val responseUserWrites: Writes[AggregatedUser] = (
    (JsPath \ "id").write[String] and
      (JsPath \ "user").write[User] and
      (JsPath \ "address").write[Address] and
      (JsPath \ "bankAccount").write[BankAccount] and
      (JsPath \ "email").write[String]
    )(unlift(AggregatedUser.unapply))

  case class UpdateUser(val password: Option[String], val email: Option[String], var firstName: Option[String], var lastName: Option[String], var language: Option[String])
  implicit val updateUserReads: Reads[UpdateUser] = (
    (JsPath \ "password").readNullable[String](minLength[String](2)) and
      (JsPath \ "email").readNullable[String](Reads.email) and
      (JsPath \ "firstName").readNullable[String](minLength[String](2)) and
      (JsPath \ "lastName").readNullable[String](minLength[String](2)) and
      (JsPath \ "language").readNullable[String](minLength[String](2))
    )(UpdateUser.apply _)


  /* Todo:
  ** Users **
  * ~Create~ > Post 200/500
  * ~Read~ > Get 200/500
  *
  * * Users/Id **
  * ~Read~    > Get
  * ~Update~  > Put
  * ~Delte~   > Delete
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

  def GetUser(userId: String) = Action {
    try {
      val user: User = users.getUser(userId)
      val aggregatedUser: AggregatedUser = new AggregatedUser(user.id, user, user.address, user.bankAccount, user.credentials.mail)
      println(aggregatedUser)
      Ok(Json.toJson(aggregatedUser))

    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

  def UpdateUser(userId: String) = Action { request =>
    val json = request.body.asJson.get
    try {
      val generatedUser = json.as[UpdateUser]

      val user: User = users.getUser(userId)

      println(generatedUser)

      if (generatedUser.email.isDefined) user.credentials.mail = generatedUser.email.get
      if (generatedUser.firstName != None) user.firstName = generatedUser.firstName.get
      if (generatedUser.lastName != None) user.lastName = generatedUser.lastName.get
      if (generatedUser.language != None) user.language = generatedUser.language.get
      if (generatedUser.password != None) user.credentials = new Credentials(user.credentials.mail, generatedUser.password.get)

      val aggregatedUser: AggregatedUser = new AggregatedUser(user.id, user, user.address, user.bankAccount, user.credentials.mail)
      println(aggregatedUser)

      val response: String =
        """
          |{
          | "user": "%s",
          | "message": "User updated"
          |}
        """.stripMargin

      Ok(response.format(Json.toJson(aggregatedUser)))

    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

  def DeleteUser(userId: String) = Action { request =>
    try {
      var msg: String = ""
      if (users.userExists(userId)) {
        users.deleteUser(userId)
        msg = "User deleted"
      } else {
        msg ="User does not exist"
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

