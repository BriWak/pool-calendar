package controllers.auth

import com.google.inject.Inject
import models.TeamRequest
import play.api.mvc._
import services.FixtureService

import scala.concurrent.{ExecutionContext, Future}

class TeamAction @Inject()(val parser: BodyParsers.Default,
                           fixtureService: FixtureService)
                          (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[TeamRequest, AnyContent] with ActionTransformer[Request, TeamRequest] {

  override def transform[A](request: Request[A]): Future[TeamRequest[A]] = {
    fixtureService.getAllTeamNames.map { teams =>
      models.TeamRequest(teams, request)
    }
  }
}
