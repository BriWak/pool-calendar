package services

import com.google.inject.Singleton
import org.mindrot.jbcrypt.BCrypt

@Singleton
class PasswordService {

   def getHash(str: String) : String = {
      BCrypt.hashpw(str, BCrypt.gensalt())
    }
    def checkHash(str: String, strHashed: String): Boolean = {
      BCrypt.checkpw(str,strHashed)
    }

}
