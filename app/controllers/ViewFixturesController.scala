package controllers

import javax.inject._
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.FixtureService
import views.html.ViewFixturesPage

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ViewFixturesController @Inject()(cc: ControllerComponents,
                                       fixtureService: FixtureService,
                                       viewFixturesPage: ViewFixturesPage) extends AbstractController(cc) with I18nSupport {

  def onPageLoad(teamName: String): Action[AnyContent] = Action.async {
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
