package com.praphull.finance

import java.util.Date

import org.joda.time.{DateTime, Days}

/** Simplified Date representation */
trait DateRep extends Ordered[DateRep] {
  def year: Int

  def month: Int

  def date: Int

  private[finance] def asNumber: Int

  private[finance] val underlying: DateTime

  override final def compare(that: DateRep): Int = asNumber - that.asNumber

  override def toString: String = s"$year-$month-$date"
}

object DateRep {
  private val epoch = new DateTime(1990, 1, 1, 0, 0, 0)

  private case class DateRepImpl(override val year: Int, override val month: Int, override val date: Int,
                                 override val underlying: DateTime) extends DateRep {
    require(year >= 1970, "Only dates after 1970-01-01 are allowed")
    override private[finance] lazy val asNumber = Days.daysBetween(epoch, underlying).getDays
  }

  private val days: Map[Int, Int] = Seq(Seq(1, 3, 5, 7, 8, 10, 12) -> 31, Seq(2) -> 28, Seq(4, 6, 9, 11) -> 30).
    flatMap { case (ml, d) => ml.map(m => (m, d)) }.toMap

  def apply(year: Int, month: Int, date: Int): DateRep = {
    require(year >= 1970, "Only positive year values are allowed")
    require(month >= 1 && month <= 12, s"Invalid month value $month, allowed: 1-12")
    require(date >= 1 && date <= (days(month) + (if (month == 2 && year % 4 == 0) 1 else 0)), s"Invalid date value $date for year $year and month $month")
    DateRepImpl(year, month, date, new DateTime(year, month, date, 0, 0, 0))
  }

  def apply(date: DateTime): DateRep =
    DateRepImpl(date.getYear, date.getMonthOfYear, date.getDayOfMonth, date)

  def apply(date: Long): DateRep = apply(new DateTime(date))

  def apply(date: Date): DateRep = apply(date.getTime)

  def apply(string: String): DateRep = apply(DateTime.parse(string))

  def nextDate(dateRep: DateRep): DateRep = DateRep(dateRep.underlying.plusDays(1))

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
