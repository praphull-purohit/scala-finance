package com.praphull.finance

import org.joda.time.DateTime
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DateRepTestSuite extends AnyFunSuite with Matchers {
  test("DateRep shouldn't allow dates earlier than 1970-01-01") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(1969, 12, 31)
    }
  }

  test("DateRep shouldn't allow month < 1") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(2020, -1, 1)
    }
  }
  test("DateRep shouldn't allow month > 12") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(2020, 13, 1)
    }
  }

  test("DateRep shouldn't allow date < 1") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(2020, 1, 0)
    }
  }
  test("DateRep should allow date = 1") {
    DateRep(2020, 1, 1)
  }

  test("DateRep shouldn't allow date > 31 for 31-day month") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(2020, 1, 32)
    }
  }

  test("DateRep should allow date = 31 for 31-day month") {
    DateRep(2020, 7, 31)
  }

  test("DateRep shouldn't allow date > 30 for 30-day month") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(2020, 6, 31)
    }
  }

  test("DateRep should allow date = 30 for 30-day month") {
    DateRep(2020, 6, 30)
  }

  test("DateRep shouldn't allow date > 29 for February in a leap year") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(2020, 2, 30)
    }
  }

  test("DateRep should allow date = 29 for February in a leap year") {
    DateRep(2020, 2, 29)
  }

  test("DateRep shouldn't allow date > 28 for February in a non-leap year") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(2021, 2, 29)
    }
  }

  test("DateRep should allow date = 28 for February in a non-leap year") {
    DateRep(2021, 2, 28)
  }

  test("DateRep.nextDate should return correct date") {
    val res1 = DateRep.nextDate(DateRep(2020, 2, 29))
    val res2 = DateRep.nextDate(DateRep(2021, 2, 28))
    val expected1 = DateRep(2020, 3, 1)
    val expected2 = DateRep(2021, 3, 1)
    res1 shouldEqual expected1
    res2 shouldEqual expected2
  }

  test("DateRep (via JodaTime constructor) shouldn't allow dates earlier than 1970-01-01") {
    an[IllegalArgumentException] shouldBe thrownBy {
      DateRep(new DateTime(-1969, 12, 31, 23, 59, 59))
    }
  }
}
