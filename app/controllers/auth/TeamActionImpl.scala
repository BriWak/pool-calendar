package controllers.auth

import com.google.inject.{ImplementedBy, Inject}
import models.TeamRequest
import play.api.mvc._
import services.FixtureService

import scala.concurrent.{ExecutionContext, Future}

class TeamActionImpl @Inject()(val parser: BodyParsers.Default,
                               fixtureService: FixtureService)
                              (implicit val executionContext: ExecutionContext)
  extends TeamAction {

  override def transform[A](request: Request[A]): Future[TeamRequest[A]] = {
    fixtureService.getAllTeamNames.map { teams =>
      models.TeamRequest(teams, request)
    }
  }
}

@ImplementedBy(classOf[TeamActionImpl])
trait TeamAction extends ActionBuilder[TeamRequest, AnyContent] with ActionTransformer[Request, TeamRequest]
