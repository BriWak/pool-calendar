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
    () => keepAppAlive()
  }(system.dispatcher)

  private def keepAppAlive(): Unit = {
    ws.url(s"${appConfig.baseUrl}${controllers.routes.KeepAliveController.keepAlive.url}")
      .get()
      .map { response =>
        if (response.status == OK) system.log.info("Keep-alive succeeded")
        else system.log.warning(s"Keep-alive failed: ${response.status}")
      }
      .recover { case ex => system.log.error(ex, "Keep-alive request failed") }
  }
}
