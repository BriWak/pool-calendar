package controllers

import base.SpecBase
import controllers.actions.FakeTeamAction
import controllers.auth.TeamAction
import models.{Team, UserSession}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import services.{AuthService, FixtureService}
import views.html.{HomePage, LoginPage}

import scala.concurrent.Future

class LoginControllerSpec extends SpecBase {

  private val mockAuthService = mock[AuthService]
  private val fakeTeamAction = app.injector.instanceOf[TeamAction]
  private val view = app.injector.instanceOf[LoginPage]

  "LoginController onPageLoad" should {

    "render the login page from a new instance of controller" in {

      val controller = new LoginController(stubControllerComponents(), fakeTeamAction, mockAuthService, view)
      val result = controller.onPageLoad().apply(FakeRequest(GET, "/login"))

      status(result) mustBe UNAUTHORIZED
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Enter your login details")
    }

    "render the login page from the application" in {
      val controller = inject[LoginController]
      val result = controller.onPageLoad().apply(FakeRequest())

      status(result) mustBe UNAUTHORIZED
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Enter your login details")
    }

    "render the login page from the router" in {
      val application: Application =
        new GuiceApplicationBuilder()
          .overrides(
            api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
            api.inject.bind[AuthService].toInstance(mockAuthService)
          ).build

      val result = route(application, FakeRequest(GET, "/login")).get

      status(result) mustBe UNAUTHORIZED
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Enter your login details")
    }
  }

  "LoginController onSubmit" should {

    "redirect to the UploadController when valid login details are provided" in {
      when(mockAuthService.checkCredentials(any(), any())).thenReturn(Future.successful(Some(UserSession("username","fakeUuid"))))

      val controller = new LoginController(stubControllerComponents(), fakeTeamAction, mockAuthService, view)
      val result = controller.onSubmit().apply(FakeRequest(POST, "/login").withFormUrlEncodedBody("username" -> "username", "password" -> "password"))

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.UploadController.onPageLoad().url
    }

    "return a BadRequest when no login details are provided" in {
      when(mockAuthService.checkCredentials(any(), any())).thenReturn(Future.successful(None))

      val controller = new LoginController(stubControllerComponents(), fakeTeamAction, mockAuthService, view)
      val result = controller.onSubmit().apply(FakeRequest(POST, "/login"))

      status(result) mustBe BAD_REQUEST
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Enter your login details")
    }

    "return a Unauthorized when invalid login details are provided" in {
      when(mockAuthService.checkCredentials(any(), any())).thenReturn(Future.successful(None))

      val controller = new LoginController(stubControllerComponents(), fakeTeamAction, mockAuthService, view)
      val result = controller.onSubmit().apply(FakeRequest(POST, "/login").withFormUrlEncodedBody("username" -> "username", "password" -> "password"))

      status(result) mustBe UNAUTHORIZED
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Enter your login details")
    }
  }
}
