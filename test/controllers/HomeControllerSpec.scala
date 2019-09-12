package controllers

import models.Team
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import services.FixtureService

class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar {

  private val mockFixtureService = mock[FixtureService]

  "HomeController GET for index page" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents(), mockFixtureService)
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("BDPL Fixture Downloader")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("BDPL Fixture Downloader")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("BDPL Fixture Downloader")
    }
  }

  "HomeController GET for download" should {

    "create the download from a new instance of controller" in {
      when(mockFixtureService.getTeamFromName(any())).thenReturn(Some(Team("name","venue",1)))
      when(mockFixtureService.createCalendar(any())).thenReturn("calendar")

      val controller = new HomeController(stubControllerComponents(), mockFixtureService)
      val result = controller.downloadCalendar().apply(FakeRequest(GET, "/download").withFormUrlEncodedBody("name" -> "validName"))

      status(result) mustBe OK
      contentType(result) mustBe Some("text/calendar")
      contentAsString(result) must include ("calendar")
    }
  }
}
