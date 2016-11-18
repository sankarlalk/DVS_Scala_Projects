/*


query,(select GUID,STAGE_NUM,BODY,BODY_FULL_BC,BODY_COMP_BC,BODY_COMP_CODE,DEL_FLAG,CHUNK_COMP_OFFSETS,CHUNK_COUNT from $DOCschema.content ) 3:23 PM
this is the select query used for this 3:23 PM
it has all the col name also 3:23 PM
so u can answer in details in next interview

 */

package com.trgr.ecp.platform.spark

import co.cask.cdap.api.metrics.Metrics
import co.cask.cdap.api.spark.SparkExecutionContext
import co.cask.cdap.api.spark.SparkMain
import co.cask.cdap.api.workflow.WorkflowToken
import com.trgr.ecp.platform.{AppConstants, AppProperties}
import com.trgr.ecp.platform.workflow.actions.spark.DBBulkPullSparkHelper
import com.trgr.ecp.platform.workflow.exception.DBBulkLoadException
import com.trgr.ecp.platform.workflow.mappingConfig.MappingConfigParser
import com.trgr.ecp.platform.workflow.util.WorkflowTokenHelper
import jdk.nashorn.internal.ir.CatchNode
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tachyon.thrift.FileDoesNotExistException

import scala.collection.immutable.HashMap
/**
  * Created by U0122207 on 6/11/2016.
  */
class ScalaSparkMain extends SparkMain {

  val LOG: Logger = LoggerFactory.getLogger(classOf[ScalaSparkMain])



  override def run(implicit sec: SparkExecutionContext): Unit = {
    val metrics : Metrics = sec.getMetrics
    // LOG.info("======== Inside Spark Main ===========")
    // println("======== Inside Spark Main ===========")
    val listOfDbObjectsForSql = WorkflowTokenHelper.getDbUrlAndSchema(sec.getWorkflowToken.get, sec.getSpecification.getProperties)
    import scala.collection.JavaConversions._
    val sc = new org.apache.spark.SparkContext
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._
    sqlContext setConf ("spark.sql.avro.compression.codec", "deflate")
    sqlContext setConf ("spark.sql.avro.deflate.level", "1")

    // LOG.info("======== Spark Main : Got "+listOfDbObjectsForSql.size()+" database information from which data to be sqooped ===========")
    // println("======== Spark Main : Got "+listOfDbObjectsForSql.size()+" database information from which data to be sqooped ===========")
    if(listOfDbObjectsForSql == null || listOfDbObjectsForSql.size() <= 0){
      LOG.error("No database details found for Spark action.")
      throw new RuntimeException("No database details found for Spark action.")
    }

    var recordCount : Long = 0
    var iteration : Int = 0
    for (optionsForSql <- listOfDbObjectsForSql) {
      //println("===== Iteration "+iteration+" ==========")
      // LOG.info("======== Spark Main : Sqooping for "+optionsForSql.get(AppConstants.DB_CONN_URL)+":"+optionsForSql.get(AppConstants.MAPPING_FILE_QUERY) + "===========")
      // println("======== Spark Main : Sqooping for "+optionsForSql.get(AppConstants.DB_CONN_URL)+":"+optionsForSql.get(AppConstants.MAPPING_FILE_QUERY) + "===========")

      /*println("======== Spark Main : TEST Writing to Avro DONE ===========")
      val driver = "oracle.jdbc.driver.OracleDriver"
      //Testing should work
      val testOptions = Map(
        "driver" -> "oracle.jdbc.driver.OracleDriver",
        "url" -> "jdbc:oracle:thin:docr/docr@graybay-vip.int.westgroup.com/gsa02z.int.westgroup.com",
        "numPartitions" -> "1",
        "dbtable" -> "(select GUID,STAGE_NUM,BODY,BODY_FULL_BC,BODY_COMP_BC,BODY_COMP_CODE,DEL_FLAG,CHUNK_COMP_OFFSETS,CHUNK_COUNT from wgsipa08.content where stage_num<=8487)")

      println("testOptions for testSqoop : "+testOptions.toString())

      val testDf = sqlContext.read.format("jdbc").options(testOptions)
      println("TEST Before load")
      val testDf1 = testDf.load()
      println("======== Spark Main : TEST Writing to Avro ===========")
      testDf1.write.format("com.databricks.spark.avro").mode(SaveMode.Append).save("SHYAM_TEST")*/

      var options = Map(
        "driver" -> "oracle.jdbc.driver.OracleDriver",
        "url" -> optionsForSql.get(AppConstants.DB_CONN_URL),
        "numPartitions" -> "1",
        "dbtable" -> optionsForSql.get(AppConstants.MAPPING_FILE_QUERY))

      // println("Options for sqoop : "+options.toString())

      var dataframeReader = sqlContext.read.format("jdbc").options(options)

      // println("Staging directory "+optionsForSql.get(AppConstants.STAGING_AVRO))

      LOG.info("Before load")
      // println("Before load")
      var df : DataFrame = null
      try{
        df = dataframeReader.load()
      }catch {
        case e :Exception  =>{
          LOG.error("Error occurred while connecting to database or loading to data to dataframe.")
          throw new RuntimeException("Error occurred while connecting to database or loading to data to dataframe.", e);
        }
      }


      LOG.info("======== Spark Main : Writing to Avro ===========")
      //println("======== Spark Main : Writing to Avro ===========")
      try{
        recordCount += df.count()
        // LOG.info("=== NUMBER OF RECORDS SQOOPED FOR "+sec.getSpecification.getProperties.get("collectionType").toString+"_"+sec.getWorkflowToken.get.get(AppProperties.COLLECTION_NAME).toString +"in iteration"+iteration+":"+recordCount)
        // println("=== NUMBER OF RECORDS SQOOPED FOR "+sec.getSpecification.getProperties.get("collectionType").toString+"_"+sec.getWorkflowToken.get.get(AppProperties.COLLECTION_NAME).toString +"in iteration"+iteration+":"+recordCount)
        metrics.gauge(sec.getSpecification.getProperties.get("collectionType").toString+"_"+sec.getWorkflowToken.get.get(AppProperties.COLLECTION_NAME).toString+"AvroCount"+iteration, recordCount)
        sec.getWorkflowToken.get.put(AppProperties.METRIC_DB_RECORD_COUNT, recordCount.toString)
        df.write.format("com.databricks.spark.avro").mode(SaveMode.Append).save(optionsForSql.get(AppConstants.STAGING_AVRO))
      }catch {
        case e :Exception  =>{
          LOG.error("Error occurred while writing the data to Avro.")
          throw new RuntimeException("Error occurred while writing the data to Avro.", e);
        }
      }

      //LOG.info("======== Spark Main : Writing to Avro completed ===========")
      //println("======== Spark Main : Writing to Avro completed ===========")

      options = null
      dataframeReader = null
      df = null
      iteration = iteration + 1

    }
    //LOG.info("=== TOTAL NUMBER OF RECORDS SQOOPED FOR "+sec.getSpecification.getProperties.get("collectionType").toString+"_"+sec.getWorkflowToken.get.get(AppProperties.COLLECTION_NAME).toString +":"+recordCount)
    //println("=== TOTAL NUMBER OF RECORDS SQOOPED FOR "+sec.getSpecification.getProperties.get("collectionType").toString+"_"+sec.getWorkflowToken.get.get(AppProperties.COLLECTION_NAME).toString +":"+recordCount)
    metrics.gauge(sec.getSpecification.getProperties.get("collectionType").toString+"_"+sec.getWorkflowToken.get.get(AppProperties.COLLECTION_NAME).toString+"TotalAvroCount", recordCount)
    /*}catch {
      case e : Exception => {
        LOG.info("======== Reached Exception block in Sprak Main ========")
        println("======== Reached Exception block in Sprak Main ========")
        throw new Exception(e.getStackTraceString)
      }*/
  }


  /* val df = sqlContext.read.format("jdbc").options(Map(
     "driver" -> "oracle.jdbc.driver.OracleDriver",
     "url" -> "jdbc:oracle:thin:docu/docu@waferville.int.westgroup.com/wla25a.int.westgroup.com",
     "numPartitions" -> "1",
     "dbtable" -> "(select GUID, STAGE_NUM from wcshtribal.META_CONTENT WHERE STAGE_NUM <=39 and guid='I284ba1f9cdba11dea82ab9f4ee295c21')")).load()
*/

  //df.write.format("com.databricks.spark.avro").save(AppConstants.STAGING_AVRO)


  //}

}
/**
  * Created by U0123301 on 10/27/2016.
  */

