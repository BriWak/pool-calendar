package controllers

import controllers.auth.TeamAction
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.FixtureService
import views.html.ViewFixturesPage

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ViewFixturesController @Inject()(cc: ControllerComponents,
                                       teamAction: TeamAction,
                                       fixtureService: FixtureService,
                                       viewFixturesPage: ViewFixturesPage) extends AbstractController(cc) with I18nSupport {

  def onPageLoad(teamName: String): Action[AnyContent] = teamAction.async {
    implicit request =>
      for {
        teamOption <- fixtureService.getTeamFromName(teamName)
        team = teamOption.getOrElse(throw new Exception)
        fixtures <- fixtureService.getAllFixturesForTeam(team)
      } yield {
        Ok(viewFixturesPage(fixtures, teamName))
      }
  }



}
