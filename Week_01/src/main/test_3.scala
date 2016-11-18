package src.main

/**
  * Created by U0123301 on 10/5/2016.
  */
object test_3 extends App{

  println("Bye")

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
}
