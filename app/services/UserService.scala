package services

import javax.inject._
import java.util.UUID.randomUUID
import models.{Credentials, User}

trait UserService{
  def getUser(id: String): User
  def addUser(id: String, user: User)
  def userExists(id:String): Boolean
  def addNewuser(password: String, email: String, firstName: String, lastName: String, language: String): String
}

class Users extends UserService {
  private var users: Map[String, User] = Map()

  override def addNewuser(password: String, email: String, firstName: String, lastName: String, language: String): String ={
    val id = randomUUID().toString
    val creds: Credentials = new Credentials(email, password)
    val user: User = new User(id, creds, firstName, lastName, language)
    addUser(id, user)
    return id
  }


  override def addUser(id: String, user: User){
    users += (id -> user)
  }

  @throws(classOf[Exception])
  override def getUser(id: String): User ={
    if (userExists(id)){
      val found: User = users(id)
      return found
    }
    throw new Exception("User not found")
  }

  override def userExists(id: String): Boolean ={
    users contains(id)
  }

}