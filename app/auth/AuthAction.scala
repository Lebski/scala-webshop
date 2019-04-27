package auth

import javax.inject.Inject
import play.api.http.HeaderNames
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


// A custom request type to hold our JWT claims, we can pass these on to the
// handling action
case class UserRequest[A](userId: String, request: Request[A]) extends WrappedRequest[A](request)

// Our custom action implementation
class AuthAction @Inject()(bodyParser: BodyParser[AnyContent], authService: AuthService)(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser

  override protected def executionContext: ExecutionContext = ec

  // A regex for parsing the Authorization header value
  private val headerTokenRegex =
    """Bearer (.+?)""".r

  // Called when a request is invoked. We should validate the bearer token here
  // and allow the request to proceed if it is valid.
  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>

      val (success, userIdOrError) = authService.parsingCredentials(token)
      if (success) {
        block(UserRequest(userIdOrError, request))
      } else Future.successful(Results.Unauthorized(userIdOrError))

    } getOrElse Future.successful(Results.Unauthorized) // no token was sent - return 401

  // Helper for extracting the token value
  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case token => token
    }
}

