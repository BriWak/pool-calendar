package controllers

import base.SpecBase
import controllers.actions.FakeTeamAction
import controllers.auth.TeamAction
import models.{Fixture, FixtureList, Team}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import services.FixtureService
import utils.DateHelper.convertStringToDate
import views.html.{HomePage, ViewFixturesPage}

import scala.concurrent.Future

class ViewFixturesControllerSpec extends SpecBase {

  private val mockFixtureService = mock[FixtureService]
  private val fakeTeamAction = app.injector.instanceOf[TeamAction]
  private val view = app.injector.instanceOf[ViewFixturesPage]

  private val fixtures = List(
    Fixture(convertStringToDate("05/09/19"), Team(3, "Comrades A"), Team(8, "Annitsford Irish B"))
  )

  "ViewFixturesController GET for index page" should {

    "render the fixtures page from a new instance of controller" in {
      when(mockFixtureService.getTeamFromName(any())).thenReturn(Future.successful(Some(Team(8, "Annitsford Irish B"))))
      when(mockFixtureService.getAllFixturesForTeam(any())).thenReturn(Future.successful(fixtures))

      val controller = new ViewFixturesController(stubControllerComponents(), fakeTeamAction, mockFixtureService, view)
      val result = controller.onPageLoad("Annitsford Irish B").apply(fakeRequest)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Annitsford Irish B")
    }

    "render the fixtures page from the application" in {
      val app: Application =
        new GuiceApplicationBuilder()
          .overrides(
            api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
            api.inject.bind[FixtureService].toInstance(mockFixtureService)
          ).build

      when(mockFixtureService.getTeamFromName(any())).thenReturn(Future.successful(Some(Team(8, "Annitsford Irish B"))))
      when(mockFixtureService.getAllFixturesForTeam(any())).thenReturn(Future.successful(fixtures))

      val controller = app.injector.instanceOf[ViewFixturesController]
      val result = controller.onPageLoad("Annitsford Irish B").apply(fakeRequest)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Annitsford Irish B")
    }

    "render the fixtures page from the router" in {
      val application: Application =
        new GuiceApplicationBuilder()
          .overrides(
            api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
            api.inject.bind[FixtureService].toInstance(mockFixtureService)
          ).build

      when(mockFixtureService.getTeamFromName(any())).thenReturn(Future.successful(Some(Team(8, "Annitsford Irish B"))))
      when(mockFixtureService.getAllFixturesForTeam(any())).thenReturn(Future.successful(fixtures))

      val result = route(application, FakeRequest(GET, "/fixtures/Annitsford%20Irish%20B")).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Annitsford Irish B")
    }
  }
}
