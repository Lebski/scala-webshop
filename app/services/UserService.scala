package services

import javax.inject._
import models.User

trait UserService{
  def getUser(id: String): User
  def addUser(id: String, user: User)
  def userExists(id:String): Boolean
}

@Singleton
class Users extends UserService {
  private var users: Map[String, User] = Map()

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