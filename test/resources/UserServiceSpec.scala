import models.User
import org.scalatestplus.play._
import services.Users

class UserServiceSpec extends PlaySpec {
  "A UserService" must {
    "create a User with password and retrieve it" in {
      var users: Users = new Users
      val exampleMail: String = "email@example.com"
      val userId: String = users.addNewuser("examplePass", exampleMail, "Max", "Müller", "de")
      val user: User = users.getUser(userId)
      users.userExists(user.id) mustBe true
      user.credentials.mail mustBe exampleMail
    }


    "delete a User" in {
      var users: Users = new Users
      val exampleMail: String = "email@example.com"
      val userId: String = users.addNewuser("examplePass", exampleMail, "Max", "Müller", "de")
      val user: User = users.getUser(userId)
      users.userExists(user.id) mustBe true
      users.deleteUser(user.id)
      users.userExists(user.id) mustBe false
    }

    "return a List of all User Ids" in {
      var users: Users = new Users
      val exampleMail: String = "email@example.com"
      val userId: String = users.addNewuser("examplePass", exampleMail, "Max", "Müller", "de")
      val userId1: String = users.addNewuser("examplePass", exampleMail, "Max", "Müller", "de")
      val userIds: Iterable[String] = users.getAllUserIds()
      userIds.exists(x => {
        x == userId
      }) mustBe true
      userIds.exists(x => {
        x == userId1
      }) mustBe true
    }

    "return a List of all Users" in {
      var users: Users = new Users
      val exampleMail: String = "email@example.com"
      val userId: String = users.addNewuser("examplePass", exampleMail, "Max", "Müller", "de")
      val userId1: String = users.addNewuser("examplePass", exampleMail, "Max", "Müller", "de")
      val allUsers: Iterable[User] = users.getAllUsers()
      allUsers.exists(x => {
        x.id == userId
      }) mustBe true
      allUsers.exists(x => {
        x.id == userId1
      }) mustBe true
    }

  }
}

