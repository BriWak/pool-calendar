package controllers

import controllers.auth.TeamAction
import forms.TeamForm
import javax.inject._
import models.League
import play.api.mvc._
import services.FixtureService
import views.html.HomePage
import utils.LeagueHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               teamAction: TeamAction,
                               fixtureService: FixtureService,
                               homePage: HomePage
                              ) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def index(): Action[AnyContent] = teamAction.async { implicit request =>
    Future.sequence {
      leagues.map { league =>
        fixtureService.getLeague(league)
      }
    }.map { allLeagues =>
      Ok(homePage(TeamForm.form, allLeagues))
    }
  }

  def downloadCalendar(): Action[AnyContent] = teamAction.async {

  implicit request =>
    TeamForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(formWithErrors.errors.toString))
      },
      formData => {
        for {
          teamOption <- fixtureService.getTeamFromName(formData.teamName)
          team = teamOption.getOrElse(throw new Exception)
          calendar <- fixtureService.createCalendar(team)
        } yield {
            Ok(calendar).as("text/calendar").withHeaders(
              "Content-Disposition" -> s"attachment; filename=${team.name} Fixtures.ics"
            )
          }
      }
    )
  }

}
