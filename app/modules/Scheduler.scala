package modules

import akka.actor.ActorSystem
import conf.ApplicationConfig
import play.api.http.Status.OK
import play.api.inject._
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

class SchedulerModule extends SimpleModule(bind[Scheduler].toSelf.eagerly())

class Scheduler @Inject()(system: ActorSystem, ws: WSClient, appConfig: ApplicationConfig)(implicit ec: ExecutionContext) {
    system.scheduler.scheduleWithFixedDelay(initialDelay = 0.seconds, delay = 10.minutes) {
      () => makeHttpRequestToKeepAlive()
    }(system.dispatcher)

  private def makeHttpRequestToKeepAlive(): Unit = {
    val keepAliveUrl = (s"${appConfig.baseUrl}${controllers.routes.KeepAliveController.keepAlive.url}")

    ws.url(keepAliveUrl)
      .get()
      .map { response =>
        response.status match {
          case OK =>
            system.log.info(s"HTTP call successful. Status: 200")
          case _ =>
            system.log.info(s"HTTP call failed. Error: ${response.body}")
        }
      }
  }
}
