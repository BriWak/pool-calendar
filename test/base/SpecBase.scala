package base

import controllers.actions.{FakeAuthAction, FakeTeamAction}
import controllers.auth.{AuthAction, TeamAction}
import org.scalatest.matchers.must.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, BodyParsers, Request}
import play.api.test.Helpers.GET
import play.api.test.{FakeRequest, Injecting}

import scala.concurrent.ExecutionContext

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with Matchers with Injecting with MockitoSugar with ScalaFutures {

  def fakeRequest: Request[AnyContentAsEmpty.type] = FakeRequest(GET, "/")

  def messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  val bodyParsers: BodyParsers.Default = app.injector.instanceOf[BodyParsers.Default]

  implicit def executionContext = app.injector.instanceOf[ExecutionContext]

  override lazy val app: Application =
    new GuiceApplicationBuilder()
      .overrides(
        api.inject.bind[TeamAction].toInstance(new FakeTeamAction(bodyParsers)),
        api.inject.bind[AuthAction].toInstance(new FakeAuthAction(bodyParsers))
      ).build
}
