import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}
object sparkJDBC {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ORCL db")
      .setMaster("local[4]")
    val sc = new SparkContext(conf)
    var sqlContext = new SQLContext(sc)
    val employees = sqlContext.load("jdbc",
      Map("url" -> "jdbc:oracle:thin:hr/hr@//localhost:1521/pdborcl",
        "dbtable" -> "hr.employees"))
    val departments  = sqlContext.load("jdbc",
      Map("url" -> "jdbc:oracle:thin:hr/hr@//localhost:1521/pdborcl",
        "dbtable" -> "hr.departments"))
    employees.
    val empDepartments = employees.join(departments,
      employees.col("DEPARTMENT_ID")===departments("DEPARTMENT_ID"))
    empDepartments.foreach(println)
    empDepartments.printSchema()
  }
}/**
  * Created by U0123301 on 11/3/2016.
  */

