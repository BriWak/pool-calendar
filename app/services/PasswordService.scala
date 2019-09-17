package services

import org.mindrot.jbcrypt.BCrypt

class PasswordService {

   def getHash(str: String) : String = {
      BCrypt.hashpw(str, BCrypt.gensalt())
    }
    def checkHash(str: String, strHashed: String): Boolean = {
      BCrypt.checkpw(str,strHashed)
    }

}
