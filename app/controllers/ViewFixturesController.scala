package controllers

import controllers.auth.TeamAction
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.FixtureService
import views.html.ViewFixturesPage

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewFixturesController @Inject()(cc: ControllerComponents,
                                       teamAction: TeamAction,
                                       fixtureService: FixtureService,
                                       viewFixturesPage: ViewFixturesPage)(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  def onPageLoad(teamName: String): Action[AnyContent] = teamAction.async { implicit request =>
    fixtureService.getTeamFromName(teamName).flatMap {
      case Some(team) =>
        fixtureService.getAllFixturesForTeam(team).map { fixtures =>
          Ok(viewFixturesPage(fixtures, teamName))
        }
      case None =>
        Future.successful(NotFound(s"Team '$teamName' not found"))
    }
  }
}
