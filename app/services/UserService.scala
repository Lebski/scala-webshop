package services

import java.util.UUID.randomUUID

import javax.inject._
import models.{Credentials, User}

trait UserService {
  def getUser(id: String): User

  def getAllUserIds(): Iterable[String]

  def getAllUsers(): Iterable[User]

  def deleteUser(id: String): Boolean

  def addUser(id: String, user: User)

  def userExists(id: String): Boolean

  def addNewuser(password: String, email: String, firstName: String, lastName: String, language: String): String

  def addAdmin(password: String, email: String, firstName: String, lastName: String, language: String): (Boolean, String)

  def isAdminOrOwner(id: String, requestingUser: String): Boolean

  def isAdmin(requestingUser: String): Boolean
}

@Singleton
class Users extends UserService {
  private var users: Map[String, User] = Map()
  private var admins: Map[String, Boolean] = Map()
  private var adminSet: Boolean = false

  override def addNewuser(password: String, email: String, firstName: String, lastName: String, language: String): String = {
    val id = randomUUID().toString
    val creds: Credentials = new Credentials(email, password)
    val user: User = new User(id, firstName, lastName, language)
    user.credentials = creds
    addUser(id, user)
    return id
  }


  override def addUser(id: String, user: User) {
    users += (id -> user)
  }

  @throws(classOf[Exception])
  override def getUser(id: String): User = {
    if (userExists(id)) {
      val found: User = users(id)
      return found
    }
    throw new Exception("User not found")
  }

  override def deleteUser(id: String): Boolean = {
    if (userExists(id)) {
      users = users - id
      return true
    }
    return false
  }

  override def userExists(id: String): Boolean = {
    users contains (id)
  }

  override def getAllUserIds(): Iterable[String] = {
    users.keys
  }

  override def getAllUsers(): Iterable[User] = {
    users.values
  }

  /**
    * This method creates a new user account and assings it to the admins list.
    * It get's only executed once and then the admin slot is blocked.
    * If you want to increase the amount of admins you could replace the boolean with a counter.
    * This option is chosen since we have no database and utility software.
    *
    * @param password
    * @param email
    * @param firstName
    * @param lastName
    * @param language
    * @return success, userId/Error
    */
  override def addAdmin(password: String, email: String, firstName: String, lastName: String, language: String): (Boolean, String) = {
    if (adminSet) return (false, "Admin slot already taken")
    adminSet = true
    val userId: String = addNewuser(password, email, firstName, lastName, language)
    admins += (userId -> true)
    return (true, userId)
  }

  override def isAdminOrOwner(id: String, requestingUser: String): Boolean = {
    if (id contentEquals requestingUser) return true
    if (admins contains (requestingUser)) return admins(requestingUser)
    return false
  }

  override def isAdmin(requestingUser: String): Boolean = {
    if (admins contains (requestingUser)) return admins(requestingUser)
    return false
  }


}