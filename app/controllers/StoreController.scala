package controllers

import auth.AuthAction
import javax.inject._
import models.User
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResultException, Json, Reads}
import play.api.mvc._
import services.{Store, Users}


/**
  * All operations to control the warehouse
  *
  * @param cc    standard controller components
  * @param users All collection of all users in the system
  */
@Singleton
class StoreController @Inject()(cc: ControllerComponents,
                                users: Users,
                                store: Store,
                                authAction: AuthAction) extends UserController(cc, users, authAction) {

  // Update and delete elements

  case class StoreElementUpdateOp(val updateOperation: String, val id: Option[String], val price: Option[Double], val description: Option[String])

  implicit val storeElemUpdateReads: Reads[StoreElementUpdateOp] = (
    (JsPath \ "updateOperation").read[String] and
      (JsPath \ "id").readNullable[String] and
      (JsPath \ "price").readNullable[Double] and
      (JsPath \ "description").readNullable[String]
    ) (StoreElementUpdateOp.apply _)

  case class PostStoreUpdateOp(var updates: Seq[StoreElementUpdateOp], var info: String)

  implicit val postStoreUpdateReads: Reads[PostStoreUpdateOp] = (
    (JsPath \ "updates").read[Seq[StoreElementUpdateOp]] and
      (JsPath \ "info").read[String]
    ) (PostStoreUpdateOp.apply _)

  // Add and remove elements to stock

  case class StockElementUpdateOp(updateOperation: String, id: String, amount: Int)

  implicit val stockElemUpdateReads: Reads[StockElementUpdateOp] = (
    (JsPath \ "updateOperation").read[String] and
      (JsPath \ "id").read[String] and
      (JsPath \ "amount").read[Int]
    ) (StockElementUpdateOp.apply _)

  case class PostStockUpdateOp(var updates: Seq[StockElementUpdateOp], var info: String)

  implicit val postStockUpdateReads: Reads[PostStockUpdateOp] = (
    (JsPath \ "updates").read[Seq[StockElementUpdateOp]] and
      (JsPath \ "info").read[String]
    ) (PostStockUpdateOp.apply _)


  def UpdateStore() = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    val json = request.body.asJson.get
    try {
      val storeUpdate = json.as[PostStoreUpdateOp]
      var added, updated, deleted: Int = 0

      for (update <- storeUpdate.updates) {
        update.updateOperation match {
          case "add" => (update.price, update.description) match {
            case (Some(price), Some(description)) => store.AddItem(price, description)
              added += 1
            case _ => NotAcceptable("Format is not right (Add item)")
          }
          case "update" => (update.id, update.price, update.description) match {
            case (Some(id), Some(price), Some(description)) => store.UpdateItem(id, price, description)
              updated += 1
            case _ => NotAcceptable("Format is not right (Update item)")
          }
          case "delete" => update.id match {
            case Some(id) => store.DeleteItem(id)
              deleted += 1
            case _ => NotAcceptable("Format is not right (Delete item)")
          }
        }
      }

      val response: String =
        """
          |{
          | "added": "%d",
          | "updated": "%d",
          | "deleted": "%d"
          |}
        """.stripMargin

      Ok(response.format(added, updated, deleted))
    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        NotFound("Internal error. Make sure objects exist.")
    }

  }


  def GetStore() = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      val allWares = store.GetWares()
      Ok(Json.toJson(allWares))
    } catch {
      case e: Throwable => println(e)
        NotFound("User could not be found")
    }
  }

  def GetItem(itemId: String) = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      val item = store.GetItem(itemId)

      val response: String =
        """
          |{
          | "id": "%s",
          | "price": "%f",
          | "description": "%s",
          | "stock": "%d"
          |}
        """.stripMargin

      Ok(response.format(item.id, item.price, item.description, item.stock))
    } catch {
      case e: Throwable => println(e)
        NotFound("Item could not be found")
    }
  }

  /**
    * Update single Item
    * PUT /warehouse/#item
    */
  def UpdateItem(itemId: String) = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    val json = request.body.asJson.get
    try {
      val update = json.as[StoreElementUpdateOp]

      (update.price, update.description) match {
        case (Some(price), Some(description)) => store.UpdateItem(itemId, price, description)
        case _ => NotAcceptable("Format is not right (Add item)")
      }

      val response: String =
        """
          |{
          | "id": "%s",
          | "price": "%f",
          | "description": "%s",
          | "stock": "%d"
          |}
        """.stripMargin

      val item = store.GetItem(itemId)

      Ok(response.format(item.id, item.price, item.description, item.stock))
    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        NotFound("Internal error. Make sure objects exist.")
    }

  }

  /**
    * Delete single Item
    * DELETE /warehouse/#item
    */
  def DeleteItem(itemId: String) = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")
    try {

      store.DeleteItem(itemId)

      val response: String =
        """
          |{
          | "deleted": "%s"
          |}
        """.stripMargin

      Ok(response.format(itemId))
    } catch {
      case e: Throwable => println(e)
        NotFound("Internal error. Make sure objects exist.")
    }

  }

  /**
    * Add or remove Items from Stock
    * POST /warehouse/stock
    */
  def UpdateStock() = authAction { request =>

    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    val json = request.body.asJson.get
    try {
      val stockUpdate = json.as[PostStockUpdateOp]
      var increased, decreased: Int = 0

      for (update <- stockUpdate.updates) {
        update.updateOperation match {
          case "increase" => store.IncreaseStock(update.id, update.amount)
            increased += 1

          case "decrease" => store.DecreaseStock(update.id, update.amount)
            decreased += 1

          case _ => NotAcceptable("Try to either 'increase' or 'decrease'")
        }
      }

      val response: String =
        """
          |{
          | "increased": "%d",
          | "decreased": "%d"
          |}
        """.stripMargin

      Ok(response.format(increased, decreased))
    } catch {
      case e: JsResultException => print(e)
        NotAcceptable("Format is not right")
      case e: Throwable => println(e)
        NotFound("Internal error. Make sure objects exist.")
    }
  }

  /**
    * Add or remove Items from Stock
    * GET /warehouse/stock
    */
  def GetStock() = authAction { request =>
    if (users.isAdmin(request.userId)) Results.Unauthorized("Authorized user not valid for this action")

    try {
      val stockElements = store.GetWares()
      var stockInfo = List[(String, String, Int)]()

      stockElements.foreach(item => {
        stockInfo = stockInfo :+ (item._1, item._2.description, item._2.stock)
      })

      Ok(Json.toJson(stockInfo))
    } catch {
      case e: Throwable => println(e)
        NotFound("Internal error. Can't read stock.")
    }
  }

  def CalcPrice(userId: String) = authAction { request =>
    if (users.isAdminOrOwner(userId, request.userId)) Results.Unauthorized("Authorized user not valid for this action")
    try {
      val user: User = users.getUser(userId)
      val price: Double = store.CalcPrice(user.shoppingCart)

      val response: String =
        """
          |{
          | "price": "%f",
          |}
        """.stripMargin

      Ok(response.format(price))
    } catch {
      case e: Throwable => println(e)
        NotFound("Internal error. Can't read stock.")
    }
  }

  def Checkout(userId: String) = authAction { request =>
    if (users.isAdminOrOwner(userId, request.userId)) Results.Unauthorized("Authorized user not valid for this action")
    try {
      val user: User = users.getUser(userId)
      val (success, info) = store.Checkout(user)

      val response: String =
        """
          |{
          | "info": "%s",
          | "successful": "%b"
          |}
        """.stripMargin

      Ok(response.format(info, success))
    } catch {
      case e: Throwable => println(e)
        NotFound("Internal error. Can't read stock.")
    }
  }


}