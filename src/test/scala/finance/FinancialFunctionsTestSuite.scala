package finance

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FinancialFunctionsTestSuite extends AnyFunSuite with Matchers {

  import FinancialFunctions._

  private def round(d: => Double): BigDecimal =
    BigDecimal(d).setScale(10, BigDecimal.RoundingMode.HALF_UP)

  private type XirrValues = (DateRep, Double)

  test("must have at least one negative") {
    val values = List[XirrValues](
      DateRep(2008, 1, 1) -> 10000,
      DateRep(2008, 3, 1) -> 2750,
      DateRep(2009, 4, 1) -> 2750,
      DateRep(2008, 10, 30) -> 4250,
      DateRep(2009, 2, 15) -> 3250
    )
    an[InvalidDataException] shouldBe thrownBy {
      xirr(values, None)
    }
  }

  test("must have at least one positive") {
    val values = List[XirrValues](
      DateRep(2008, 1, 1) -> -10000,
      DateRep(2008, 3, 1) -> -2750
    )
    an[InvalidDataException] shouldBe thrownBy {
      xirr(values, None)
    }
  }

  test("must not be an empty list") {
    val values = List.empty[XirrValues]
    an[EmptyValuesException] shouldBe thrownBy {
      xirr(values, None)
    }
  }

  test("starting date should be less than all other dates") {
    val values = List[XirrValues](
      DateRep(2008, 3, 1) -> 2750,
      DateRep(2008, 1, 1) -> -10000,
      DateRep(2009, 4, 1) -> 2750,
      DateRep(2008, 10, 30) -> 4250,
      DateRep(2009, 2, 15) -> 3250
    )
    an[ValuePrecedeStartingDateException] shouldBe thrownBy {
      xirr(values, None)
    }
  }

  test("calculate correct NPV") {
    val values = List[XirrValues](
      DateRep(2008, 1, 1) -> -10000,
      DateRep(2008, 3, 1) -> 2750,
      DateRep(2008, 10, 30) -> 4250,
      DateRep(2009, 2, 15) -> 3250,
      DateRep(2009, 4, 1) -> 2750
    )
    val res = xnpv(values, 0.09)
    val expected = 2086.6476020315
    round(res) shouldEqual round(expected)
  }

  test("calculate correct XIRR") {
    val values = List[XirrValues](
      DateRep(2019, 1, 24) -> -5000,
      DateRep(2019, 6, 21) -> -10000,
      DateRep(2019, 7, 30) -> -5000,
      DateRep(2019, 8, 20) -> -5000,
      DateRep(2019, 8, 23) -> -3000,
      DateRep(2020, 3, 1) -> -1500,
      DateRep(2020, 3, 23) -> -3000,
      DateRep(2020, 4, 1) -> 24259.76
    )
    val res = xirr(values, None)
    val expected = -0.35748720310015153
    round(res) shouldBe round(expected)
  }

  test("calculate correct XIRR for unordered values") {
    import DateRep._
    val values = List[XirrValues](
      DateRep(2008, 1, 1) -> -10000,
      DateRep(2008, 10, 30) -> 4250,
      (2008, 3, 1).toDateRep -> 2750,
      DateRep(2009, 4, 1) -> 2750,
      DateRep(2009, 2, 15) -> 3250
    )
    val res = xirr(values, None)
    val expected = 0.3733625335188314
    round(res) shouldEqual round(expected)
  }
}
