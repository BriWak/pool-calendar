package services

import java.util.UUID

import com.google.inject.Inject
import models.UserSession
import repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthService @Inject()(sessionRepository: SessionRepository,
                            passwordService: PasswordService) {

  val passwordHash = "$2a$10$HKdRb9WT.acL1ODZqMtpJeFLtyClZLvvpKeGH2mhNU4hxcUzYOZEW"

  def checkCredentials(username: String, password: String): Future[Option[UserSession]] = {
    if (passwordService.checkHash(s"$username/$password", passwordHash)) {
      addAuthUUID(username).map(Some(_))
    } else Future.successful(None)
  }

  def isLoggedIn(uuid: String): Future[Boolean] = {
    sessionRepository.findByUuid(uuid).map(_.isDefined)
  }

  private def addAuthUUID(username: String): Future[UserSession] = {
    val userSession = UserSession(username, UUID.randomUUID().toString)
    sessionRepository.set(userSession).map { _ =>
      userSession
    }
  }
}
