package controllers

import forms.TeamForm
import javax.inject._
import models.Team
import play.api.mvc._
import services.FixtureService
import views.html.indexPage

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class HomeController @Inject()(cc: ControllerComponents, fixtureService: FixtureService) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    fixtureService.getAllTeams.map { teams =>
      Ok(indexPage(TeamForm.form, teams))
    }
  }

  def downloadCalendar(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
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
