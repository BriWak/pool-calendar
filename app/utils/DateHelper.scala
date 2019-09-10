package utils

import java.text.SimpleDateFormat
import java.util.Date

object DateHelper {

    val DATE_FORMAT = "dd/mm/yy h:mm"

    def getDateAsString(d: Date): String = {
      val dateFormat = new SimpleDateFormat(DATE_FORMAT)
      dateFormat.format(d)
    }

    def convertStringToDate(s: String): Date = {
      val dateFormat = new SimpleDateFormat(DATE_FORMAT)
      dateFormat.parse(s"$s 20:00")
    }

}
