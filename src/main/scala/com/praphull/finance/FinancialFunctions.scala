package com.praphull.finance

trait FinancialFunctions {

  import FinancialFunctions._

  private val maxDiff = 1E-10
  private val maxCounter = 100

  //Performs validations for NPV and XIRR. Function f is of the following form:
  //startDate - first investment date, initialRate - Starting rate, remainingData - all investments except the first,
  //npv - a partial function from a given rate of return that can calculate NPV for the original dataset (values)
  private def npvBase(values: List[(DateRep, Double)], initialRate: Double)
                     (f: (Double, Double, List[(Double, Double)], Double => Double) => Double): Double = {
    val (startDate, seedAmount, remainingData) = values match {
      case (sr, sa) :: rest =>
        val sd = sr.asNumber
        val remaining = {
          val init = (List.empty[(Double, Double)], sa < 0, sa > 0)
          val (res, hasNegatives, hasPositives) = rest.foldRight(init) { case ((dateRep, amount), (resultList, hasNegatives, hasPositives)) =>
            if (dateRep.asNumber < sd) throw new ValuePrecedeStartingDateException
            ((dateRep.asNumber, amount) :: resultList, hasNegatives || amount < 0, hasPositives || amount > 0)
          }
          if (!hasNegatives) throw new InvalidDataException("negative")
          if (!hasPositives) throw new InvalidDataException("positive")
          res
        }

        (sd, sa, remaining)

      case Nil => throw new EmptyValuesException
    }

    def calculateNPV(rate: Double): Double = {
      remainingData.foldRight(seedAmount) { case ((d, p), total) =>
        total + (p / Math.pow(1 + rate, (d - startDate) / 365))
      }
    }

    f(startDate, initialRate, remainingData, calculateNPV)
  }

  /**
   * Calculates internal rate of return for a series of non-periodic cash flows
   *
   * @param values A List of dates and the cash flows on those dates, with negative values representing investment and
   *               positive value representing withdrawal
   * @param guess  Estimated rate of return. If not provided, the estimation starts with 0.1 (a return rate of 10%)
   */
  final def xirr(values: => List[(DateRep, Double)], guess: Option[Double] = None): Double = {
    npvBase(values, guess.getOrElse(0.1)) { case (startDate, initialRate, data, npv) =>
      /* Sums the derivatives against rate: https://www.derivative-calculator.net/#expr=a%2F%28x%5Ey%29
       * {{{
       * d/dx[a/(x^b)] = -abx^(-b-1) = ab/(x^(b+1)
       * a: p
       * x: (1 + rate)
       * b: (d - startDate) / 365
       *
       * d/dx[a/(x^y)] = - p * b / ( (1 + r) ^ b )
       * }}}
       */
      def sumDerivatives(rate: Double): Double = {
        data.foldRight(0.0) { case ((d, p), total) =>
          val diff = (d - startDate) / 365 //b
          total - diff * p / Math.pow(1 + rate, diff + 1)
        }
      }

      @scala.annotation.tailrec
      def calculate(rate: Double, counter: Int): Double = {
        if (counter == maxCounter) throw new TooLongComputationException
        val res = npv(rate)
        val newRate = rate - (res / sumDerivatives(rate))
        val diff = Math.abs(newRate - rate)
        if (Math.abs(res) <= maxDiff && diff <= maxDiff) newRate else {
          calculate(newRate, counter + 1)
        }
      }

      calculate(initialRate, 0)

    }
  }

  /**
   * Calculates net present value for a series of non-periodic cash flows
   *
   * @param values A List of dates and the cash flows on those dates, with negative values representing investment and
   *               positive value representing withdrawal
   * @param rate   Rate of return
   */
  final def xnpv(values: List[(DateRep, Double)], rate: Double): Double = {
    npvBase(values, rate) { case (_, _, _, getNPV) =>
      getNPV(rate)
    }
  }
}

object FinancialFunctions {

  class TooLongComputationException extends Exception("Computation exceeded maximum allowed iterations")

  class InvalidDataException(dataType: String) extends Exception(s"No $dataType values in the data")

  class EmptyValuesException extends Exception("Empty values")

  class ValuePrecedeStartingDateException extends Exception("Few date values precede the starting date")

}