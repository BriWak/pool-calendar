package controllers

import conf.ApplicationConfig
import controllers.auth.TeamAction
import forms.TeamForm
import play.api.Logging
import play.api.mvc._
import services.FixtureService
import views.html.{ErrorPage, HomePage}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(
    cc: ControllerComponents,
    teamAction: TeamAction,
    fixtureService: FixtureService,
    homePage: HomePage,
    errorPage: ErrorPage
  )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def index: Action[AnyContent] = teamAction.async { implicit request =>
    fixtureService.getAllTeams.map { teams =>
      Ok(homePage(TeamForm.form, teams))
    }
  }

  def downloadCalendar: Action[AnyContent] = teamAction.async { implicit request =>
    TeamForm.form.bindFromRequest.fold(
      formWithErrors =>
        Future.successful(BadRequest(formWithErrors.errors.toString)),

      formData =>
        fixtureService.getTeamFromName(formData.teamName).flatMap {
          case Some(team) =>
            fixtureService.createCalendar(team).map { calendar =>
              Ok(calendar)
                .as("text/calendar")
                .withHeaders(
                  "Content-Disposition" -> s"attachment; filename=${team.name} Fixtures.ics"
                )
            }
          case None =>
            Future.successful(NotFound(errorPage("Team not found")))
        }
    )
  }

}
