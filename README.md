# Financial Functions

This is a library of commonly used financial function implementation in Scala.

#### Usage
Include the following dependency in `build.sbt`
```sbt
"com.praphull" %% "scala-finance" % "0.0.1"
```

The library cross compiles to Scala 2.11, 2.12 and 2.13. Use `sbt +test` to run the tests.

To work with dates, a helper representation `DateRep` is provided, which can be constructed in the following manners:
* `DateRep(year: Int, month: Int, date: Int)`
* `DateRep(date: DateTime)` - From org.joda.time.DateTime ([Joda Time](https://www.joda.org/joda-time))
* `DateRep(date: Long)` - milliseconds since 1970-01-01T00:00:00 UTC+0000
* `DateRep(date: Date)` - From java.util.Date
* `DateRep(string: String)` - From date string (Uses DateTime.parse internally)

#### Implemented methods

##### XIRR
Usage:
```scala
import finance.DateRep
finance.xirr(List(
  DateRep(2008, 1, 1) -> -10000,
  DateRep(2008, 10, 30) -> 4250,
  DateRep(2008, 3, 1) -> 2750,
  DateRep(2009, 4, 1) -> 2750,
  DateRep(2009, 2, 15) -> 3250
), Some(0.05))
```
###### Reference
Microsoft Support - [XIRR](https://support.office.com/en-us/article/xirr-function-de1242ec-6477-445b-b11b-a303ad9adc9d)

##### XNPV
Usage:
```scala
import finance.DateRep
finance.xnpv(List(
  DateRep(2008, 1, 1) -> -10000,
  DateRep(2008, 10, 30) -> 4250,
  DateRep(2008, 3, 1) -> 2750,
  DateRep(2009, 4, 1) -> 2750,
  DateRep(2009, 2, 15) -> 3250
), 0.45)
```
###### Reference
Microsoft Support - [XNPV](https://support.microsoft.com/en-us/office/xnpv-function-1b42bbf6-370f-4532-a0eb-d67c16b664b7)
 
