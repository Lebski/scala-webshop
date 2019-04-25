package controllers

import javax.inject._
import models.User
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResultException, Json, Reads}
import play.api.mvc._
import services.Users
import auth.AuthAction



/**
  * All operation on a user in [[Users]] concering the Shopping cart
  *
  * @param cc    standard controller components
  * @param users All collection of all users in the system
  */
@Singleton
class CartController @Inject()(cc: ControllerComponents,
                               users: Users,
                               authAction: AuthAction) extends UserController(cc, users) {

  case class CartElementUpdate(val productId: String, val updateOperation: String, val quantity: Int)

  implicit val cartElemUpdateReads: Reads[CartElementUpdate] = (
    (JsPath \ "productId").read[String] and
      (JsPath \ "updateOperation").read[String] and
      (JsPath \ "quantity").read[Int]
    ) (CartElementUpdate.apply _)


  case class PostCartUpdate(var updates: Seq[CartElementUpdate], var info: String)

  implicit val postCartUpdateReads: Reads[PostCartUpdate] = (
    (JsPath \ "updates").read[Seq[CartElementUpdate]] and
      (JsPath \ "info").read[String]
    ) (PostCartUpdate.apply _)


  def UpdateCart(userId: String) = authAction { request =>

    if (request.userId != userId) Results.Unauthorized("Wrong user id")

    val json = request.body.asJson.get
    try {
      val cartUpdate = json.as[PostCartUpdate]
      val user: User = users.getUser(userId)

      for (update <- cartUpdate.updates) {
        update.updateOperation match {
          case "add" => user.shoppingCart.addCartItem(update.productId, update.quantity)
          case "remove" => user.shoppingCart.removeCartItem(update.productId, update.quantity)
          case "discard" => user.shoppingCart.discardCartItem(update.productId)
        }
      }

      Ok(Json.toJson(user.shoppingCart.getCart()))
    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }

  }

  def GetCart(userId: String) = authAction { request =>

    if (request.userId != userId) Results.Unauthorized("Wrong user id")

    try {
      val user: User = users.getUser(userId)
      Ok(Json.toJson(user.shoppingCart.getCart()))
    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

  def ResetCart(userId: String) = authAction { request =>

    if (request.userId != userId) Results.Unauthorized("Wrong user id")

    try {
      val user: User = users.getUser(userId)
      user.shoppingCart.resetCart()
      Ok(Json.toJson(user.shoppingCart.getCart()))
    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }


}


