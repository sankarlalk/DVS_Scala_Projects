package src.main

/**
  * Created by U0123301 on 10/16/2016.
  */
object ForLoop extends App{

  for (i <- 1 to 7)
  {
    println(i)
  }

  for(i <- 1 until 7)
  {
    println(i)
  }

  for (i <- 1 to 10 by 2)
  {
    println(i)
  }

  for( i <- 1 to 10 by -1)
  {
    println(i)
  }

  // For comprehension - for with yield
  val x = List(1,2,3,4)
  val y = for(e <- x) yield e+2;
  {
    println(y);
  }

  // Iterate guard
  val y1 = for(e <- x if e % 2 == 0) yield e+2;
  {
    println(y1);
  }


  val y2 = for(e <- x if e % 2 == 0 if e > 2) yield e+2;
  {
    println("Second set of iterat guard")
    println(y2);
  }

  //  OR separate the condition with ";"
  val y3 = for(e <- x if e % 2 == 0; if e > 2) yield e+2;
  {
    println("Second set of iterat guard")
    println(y3);
  }

  // Nested For loop
  for( x <- 1 to 3; z <- 1 to 5)
  {
    println(x, z);
  }

  // Value binding in for loop
  val a1 = for(x <- 1 to 8; pow = 1 << x) yield pow;
  {
    println(a1);
  }

  val a2 = for(x <- 1 to 8) yield{ val pow = 1 << x;  pow}
  {
    println(a2);
  }

  var a = 0
  // While loops doesnt work like for - It returns Unit which is null
  val a3 = while (a < 5)
    {
      println(a)
      a+=1
    }



}
