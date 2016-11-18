

def shuffle(cards: List) =
{
  shuffleLoop(n:Int, cards: List[Int], returnList: List[Int]) {
    num = Random.nextInt(noOfCard)
    returnCards = returnCards :: cards[num]

    (a, b) = cards.split(num)
    shuffle(noOfCard - 1, a ::: b, returnCards)
  }
  shuffleLoop(52, cards )
}


import org.apache.spark.sql.SQLcontext
import org.apache.spark.hive.HiveContext


val sc = new SparkContext(...)
val hiveCtx = new HiveCOntext(sc)


