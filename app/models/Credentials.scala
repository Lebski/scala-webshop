package models

import com.github.t3hnar.bcrypt._

import scala.io.Source
import scala.util.{Try,Success,Failure}

import scala.util.Try

class Credentials(var mail: String, password: String) {

  val salt = generateSalt

  var pwHash = password.bcrypt(salt)
  var result = password.isBcryptedSafe(pwHash)

  def passValid(password: String): Boolean = {
    password.isBcryptedSafe(pwHash) match {
      case Success(safe) => return safe
      case Failure(f) => println(f)
        return false
    }
  }

}
