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
}

@Singleton
class Users extends UserService {
  private var users: Map[String, User] = Map()

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


}