package controllers

import base.SpecBase
import controllers.actions.FakeTeamAction
import controllers.auth.TeamAction
import models.Team
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import services.FixtureService
import views.html.HomePage

import scala.concurrent.Future

class HomeControllerSpec extends SpecBase {

  private val mockFixtureService = mock[FixtureService]
  private val fakeTeamAction = app.injector.instanceOf[TeamAction]
  private val view = app.injector.instanceOf[HomePage]

  "HomeController GET for index page" should {

    "render the index page from a new instance of controller" in {
      when(mockFixtureService.getAllTeams).thenReturn(Future.successful(List.empty))

      val controller = new HomeController(stubControllerComponents(), fakeTeamAction, mockFixtureService, view)
      val result = controller.index().apply(fakeRequest)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("BDPL Fixture Downloader")
    }

    "render the index page from the application" in {
      val app: Application =
        new GuiceApplicationBuilder()
          .overrides(
            api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
            api.inject.bind[FixtureService].toInstance(mockFixtureService)
          ).build

      val controller = app.injector.instanceOf[HomeController]
      val result = controller.index().apply(fakeRequest)

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("BDPL Fixture Downloader")
    }

    "render the index page from the router" in {
      val application: Application =
        new GuiceApplicationBuilder()
          .overrides(
            api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
            api.inject.bind[FixtureService].toInstance(mockFixtureService)
          ).build

      val result = route(application, fakeRequest).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("BDPL Fixture Downloader")
    }
  }

  "HomeController GET for download" should {

    "create the download from a new instance of controller" in {
      when(mockFixtureService.getTeamFromName(any())).thenReturn(Future.successful(Some(Team("name","venue",1))))
      when(mockFixtureService.createCalendar(any())).thenReturn(Future.successful("calendar"))

      val controller = new HomeController(stubControllerComponents(), fakeTeamAction, mockFixtureService, view)
      val result = controller.downloadCalendar().apply(FakeRequest(GET, "/download").withFormUrlEncodedBody("name" -> "validName"))

      status(result) mustBe OK
      contentType(result) mustBe Some("text/calendar")
      contentAsString(result) must include ("calendar")
    }
  }
}
