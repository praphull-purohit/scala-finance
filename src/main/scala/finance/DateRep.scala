package finance

import java.util.Date

import org.joda.time.{DateTime, Days}

/** Simplified Date representation */
trait DateRep {
  def year: Int

  def month: Int

  def date: Int

  private[finance] def asNumber: Double

  private[finance] val underlying: DateTime
}

object DateRep {
  private val epoch = new DateTime(1990, 1, 1, 0, 0, 0)

  private class DateRepImpl(override val year: Int, override val month: Int, override val date: Int,
                            override val underlying: DateTime) extends DateRep {
    override private[finance] lazy val asNumber = Days.daysBetween(epoch, underlying).getDays.toDouble
  }

  def apply(year: Int, month: Int, date: Int): DateRep = {
    new DateRepImpl(year, month, date, new DateTime(year, month, date, 0, 0, 0))
  }

  def apply(date: DateTime): DateRep =
    new DateRepImpl(date.getYear, date.getMonthOfYear, date.getDayOfMonth, date)

  def apply(date: Long): DateRep = apply(new DateTime(date))

  def apply(date: Date): DateRep = apply(date.getTime)

  def apply(string: String): DateRep = apply(DateTime.parse(string))

  implicit class Tuple3ToDateRep(val ymd: (Int, Int, Int)) {
    def toDateRep: DateRep = apply(ymd._1, ymd._2, ymd._3)
  }

  implicit class JodaDateTimeToDateRep(val dateTime: DateTime) {
    def toDateRep: DateRep = apply(dateTime)
  }

  implicit class JavaDateToDateRep(val date: Date) {
    def toDateRep: DateRep = apply(date)
  }

  implicit class LongToDateRep(val dateInstant: Long) extends AnyVal {
    def toDateRep: DateRep = apply(dateInstant)
  }

  implicit class StringToDateRep(val string: String) extends AnyVal {
    def toDateRep: DateRep = apply(string)
  }

}
