// Functions are first class cititions in Scala = They are the heart of scala

// Nested Functions
def max(a: Int, b: Int, c: Int) = {
  def max(x: Int, y: Int) = if(x > y) x else y
  max(a, max(b,c))
}
max(2,4,1)

// Call by name
def byName(a: Int, b: Int) = { if (a > b) a else b }
byName(b=10, a=5)


// Parameter with default values:
//def default(a: Int = 0) = { 0 }
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
def sum1( x: Int, y: Int)(z: String) = {
  println(z)
  x + y
}
sum1(10,20) ("lal")
var test = sum1(1,2)_
test("sankar")

//Generic typed functions
def identity[A](a: A): A = { println(a); a}
identity[String]("lal1")
identity("Sankar1")


// Higher Order Functions

  // 1. Assign value to a function
  // 2. Assign one function to another
  // 3. Pass one function to another function as parameter
  // 4. Return a function from a function
// * Function Type = Input parameter + Return type

def double(x: Int): Int = x * 2
double(5)
val myDouble: (Int) => Int = double
myDouble(2)

val myD: (Int) => Int = (a: Int) => a * 2      // With type
val myD1 = (a: Int) => a * 2                   // Without Type
myD(20)















