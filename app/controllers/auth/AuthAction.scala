package controllers.auth

import com.google.inject.{ImplementedBy, Inject}
import play.api.mvc.Results.Redirect
import play.api.mvc._
import services.AuthService

import scala.concurrent.{ExecutionContext, Future}

class AuthActionImpl @Inject()(val parser: BodyParsers.Default,
                           authService: AuthService)(implicit val executionContext: ExecutionContext)
  extends AuthAction {

  override protected def refine[A](request: Request[A]): Future[Either[Result, Request[A]]] =
    request.session.get("UUID") match {
      case None => Future.successful(Left(Redirect(controllers.routes.LoginController.onPageLoad)))
      case Some(uuid) =>
        authService.isLoggedIn(uuid).map { loggedIn =>
          if (loggedIn) Right(request)
          else Left(Redirect(controllers.routes.LoginController.onPageLoad))
        }
    }
}

@ImplementedBy(classOf[AuthActionImpl])
trait AuthAction extends ActionBuilder[Request, AnyContent] with ActionRefiner[Request, Request]
