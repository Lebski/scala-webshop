package auth

import javax.inject._
import java.util.Date

import akka.actor.FSM.Failure
import authentikat.jwt._
import models.User
import services.Users


class AuthService @Inject()(users: Users){

  val expirationOffset: Int = 60000 * 10 // Minutes

  // WARNING: DO NEVER USE THIS SECRET KEY IN PRODCUTION
  val secretKey = "A7PvVg0iCJJB6559nuPAJEm6cnnPfAKf"


  def createToken(userId: String): String ={
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(Map("iss" -> "ScalaWebShop", "sub" -> userId, "exp" -> (new Date().getTime + expirationOffset)))
    val jwt: String = JsonWebToken(header, claimsSet, secretKey)
    return jwt
  }

  def validateUser(userId: String, password: String): (Boolean, String) ={
    val user: User = users.getUser(userId)
    if (user.credentials.passValid(password)) return (true, createToken(userId))
    return (false, "Password not valid")
  }

  def validateToken(jwt: String): Boolean = JsonWebToken.validate(jwt, secretKey)

  /**
    * Parsing the username out of a JWT token and checks if its valid
    * @param jwt A well formed JWT
    * @return Boolean -> Shows if parsing was successful
    * @return String -> Returns userId if success = true; Returns reason of failure if success = false
    */
  def parsingCredentials(jwt: String): (Boolean, String) ={

    if (!validateToken(jwt)) return (false, "JWT not valid")

    val claims: Option[Map[String, String]] = jwt match {
      case JsonWebToken(header, claimsSet, signature) =>
        claimsSet.asSimpleMap.toOption
      case x =>
        None
    }

    // Check if date is valid
    val exp: Option[String] = claims.getOrElse(Map.empty[String, String]).get("exp")
    if (exp.isDefined) {
      val now: Long = new Date().getTime
      try {
        // compare time to now (as long)
        if (exp.get.toLong < now) return (false, "Token expired")
      } catch {
        case e: NumberFormatException => return (false, "exp is no number")
      }
    } else return (false,  "Cant parse exp-date")

    val userId: Option[String] = claims.getOrElse(Map.empty[String, String]).get("sub")
    if (userId.isDefined) return (true, userId.get)
    return (false, "Cant parse userId")
  }

}
