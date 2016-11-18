package src.main

/**
  * Created by U0123301 on 10/16/2016.
  */
object Functions extends App{

  // Simple Functions
  def test(x: Int): Unit =
  {
    println("Test1")
  }
  def test1(x: Int)
  {

  }
  def test3   = "Sankarlal"
  def test4() = "Sankarlal"
  def test5(): String = "Sankarlal"
  def multiplier(x: Int, y: Int): Int = { x*y }

  // Nested Functions
  def max(a: Int, b: Int, c: Int) = {
    def max(x: Int, y: Int) = if(x > y) x else y
    max(a, max(b,c))
  }

  // Call by name
  def byName(a: Int, b: Int) = { if (a > b) a else b }
  byName(b=10, a=5)

  // Parameter with default values:
  def default(a: Int = 0) = { 0 }
  def default(a: Int = 0, b: Int = 0) = { if (a > b) a else b }
  default(10)

  // Var Arg Functions
  def sum(items: Int*): Int = {
    var total = 0
    for(i <- items) total += i
    total
  }
  sum(10,2,3,4)
  val l = List(1,2,3)
  sum(l : _*)

  // Parameter Groups
  def sum1(x: Int, y: Int)(z: String) = {
    println(z)
    x + y
  }
  sum1(10,20) ("lal")
  var test = sum1(1,2)_
  test("sankar")


  // Higher order Functions

    // 1. Assign value to a function
    // 2. Assign one function to another
    // 3. Pass one function to another function as parameter
    // 4. Return a function from a function
  // * Function Type = Input parameter + Return type

}
