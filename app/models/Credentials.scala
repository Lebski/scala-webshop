package models

import com.github.t3hnar.bcrypt._

import scala.util.Try

class Credentials(var mail: String, password: String) {

  val salt = generateSalt

  var pwHash = password.bcrypt(12)
  var result = password.isBcryptedSafe(pwHash)

  def passValid(password: String): Try[Boolean] ={
    return password.isBcryptedSafe(pwHash)
  }

}
