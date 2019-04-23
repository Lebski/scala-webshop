package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json.Json
import services.Users

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
  * Create
  * ~Read~
  * Update?
  * Delete
  *
  * * Users/Id **
  * Create
  * Read
  * Update
  * Delte
  *
  ** Shopping Cart **
  * Add
  * Remove
  * "Delete"
  *
  ** Credentials **
  * Update
  *
  ** Adress **
  * Update

   */

  /**
    * Return all Users
    * GET /users
    */
  def GetUsers = Action {
    Ok(Json.toJson(users.getAllUsers()))
  }

}

