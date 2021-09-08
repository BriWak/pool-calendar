package controllers.actions

import controllers.auth.AuthAction
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class FakeAuthAction(val parser: BodyParsers.Default) extends AuthAction {

  override protected def refine[A](request: Request[A]): Future[Either[Result, Request[A]]] = {
      Future.successful(Right(request))
  }

  override protected implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

}
