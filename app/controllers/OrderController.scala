package controllers

import auth.AuthAction
import javax.inject._
import services.Orders
import play.api.libs.json._
import play.api.mvc.{Results, _}
import services.Users

/**
  * Define CRUD-Actions on the [[Orders]] singleton object.
  * Uses the [[AuthAction]] authentication.
  *
  * @param cc    standard controller components
  * @param users All collection of all users in the system
  */
@Singleton
class OrderController @Inject()(cc: ControllerComponents,
                                users: Users,
                                authAction: AuthAction,
                                orders: Orders) extends AbstractController(cc) {

  /**
    * List orders
    * Get /order
    */
  def GetOrders() = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      val allOrders = orders.GetAllOrders()

      Ok(Json.toJson(allOrders))
    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

  /**
    * Get order by id
    * Get /order/#oderId
    */
  def GetOrder(orderId: String) = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      val order = orders.GetOrder(orderId)

      Ok(Json.toJson(order))
    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }



  /**
    * Delete order by id
    * DELETE /order/#orderId
    */
  def DeleteOrder(orderId: String) = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      orders.DeleteOrder(orderId)

      val response: String =
        """
          |{
          | "deleted": "%s"
          |}
        """.stripMargin

      Ok(response.format(orderId))
    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

}