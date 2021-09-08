package controllers.actions

import controllers.auth.TeamAction
import models.TeamRequest
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class FakeTeamAction(val parser: BodyParsers.Default) extends TeamAction {

  override def transform[A](request: Request[A]): Future[TeamRequest[A]] = {
    Future.successful(models.TeamRequest(List("Team 1", "Team 2", "Team 3"), request))
  }

  override protected implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

}
