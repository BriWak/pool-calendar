package controllers

import base.SpecBase
import controllers.actions.{FakeAuthAction, FakeTeamAction}
import controllers.auth.{AuthAction, TeamAction}
import models.{League, UserSession}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import services.{AuthService, FileService, MongoService}
import views.html.UploadPage

import scala.concurrent.Future

class UploadControllerSpec extends SpecBase {

  private val mockFileService = mock[FileService]
  private val mockMongoService = mock[MongoService]
  private val fakeTeamAction = app.injector.instanceOf[TeamAction]
  private val fakeAuthAction = app.injector.instanceOf[AuthAction]
  private val view = app.injector.instanceOf[UploadPage]

  "UploadController onPageLoad" should {

    "render the admin page from a new instance of controller" in {
      when(mockMongoService.getLeagues).thenReturn(Future.successful(List(League("Super League"))))

      val controller = new UploadController(stubControllerComponents(), fakeTeamAction, fakeAuthAction, mockFileService, mockMongoService, view)
      val result = controller.onPageLoad().apply(FakeRequest(GET, "/admin"))

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Fixtures for the following leagues are in the database:")
    }

    "render the admin page from the application" in {
      val app: Application =
        new GuiceApplicationBuilder()
          .overrides(
            api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
            api.inject.bind[AuthAction].toInstance(new FakeAuthAction(bodyParsers)),
            api.inject.bind[MongoService].toInstance(mockMongoService)
          ).build

      when(mockMongoService.getLeagues).thenReturn(Future.successful(List(League("Super League"))))

      val controller = app.injector.instanceOf[UploadController]
      val result = controller.onPageLoad().apply(FakeRequest())

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Fixtures for the following leagues are in the database:")
    }

    "render the admin page from the router" in {
      val application: Application =
        new GuiceApplicationBuilder()
          .overrides(
            api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
            api.inject.bind[AuthAction].toInstance(new FakeAuthAction(bodyParsers)),
            api.inject.bind[MongoService].toInstance(mockMongoService)
          ).build

      when(mockMongoService.getLeagues).thenReturn(Future.successful(List(League("Super League"))))

      val result = route(application, FakeRequest(GET, "/admin")).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Fixtures for the following leagues are in the database:")
    }
  }

  "UploadController resetDatabase" should {

    "reset the mongo database" in {
      when(mockMongoService.dropDatabase).thenReturn(Future.successful(true))

      val controller = new UploadController(stubControllerComponents(), fakeTeamAction, fakeAuthAction, mockFileService, mockMongoService, view)
      val result = controller.resetDatabase().apply(FakeRequest(POST, "/delete"))

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("All information has been deleted from the database.")
    }
  }

}
