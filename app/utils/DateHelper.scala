package utils

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter

object DateHelper {

  val DATE_FORMAT = "dd/MM/yy"

  val ICAL_DATE_FORMAT = "yyyyMMdd"
  val ICAL_TIME_FORMAT = "HHmmss"

  def getDateAsString(d: LocalDate): String = {
    d.format(DateTimeFormatter.ofPattern(ICAL_DATE_FORMAT))
  }

  def getTimeAsString(d: LocalTime): String = {
    d.format(DateTimeFormatter.ofPattern(ICAL_TIME_FORMAT))
  }

  def getPrintableDate(d: LocalDate): String = {
    d.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
  }

  def convertStringToDate(s: String): LocalDate = {
    LocalDate.parse(s, DateTimeFormatter.ofPattern(DATE_FORMAT))
  }


}
