package utils

import java.text.SimpleDateFormat
import java.util.Date

object DateHelper {

  val DATE_FORMAT = "dd/MM/yy HH:mm"

  val ICAL_DATE_FORMAT = "yyyyMMdd"
  val ICAL_TIME_FORMAT = "HHmmss"


  def getDateAsString(d: Date): String = {
    val dateFormat = new SimpleDateFormat(ICAL_DATE_FORMAT)
    dateFormat.format(d)
  }

  def getTimeAsString(d: Date): String = {
    val dateFormat = new SimpleDateFormat(ICAL_TIME_FORMAT)
    dateFormat.format(d)
  }

  def convertStringToDate(s: String): Date = {
    val dateFormat = new SimpleDateFormat(DATE_FORMAT)
    dateFormat.parse(s"$s 20:00")
  }


}
