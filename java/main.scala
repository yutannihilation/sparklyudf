package sparklyudf

import org.apache.spark.sql.SparkSession;

import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

object Main {
  def register_hello(spark: SparkSession, fun: String) = {
    val tb = currentMirror.mkToolBox()
    val tree = tb.parse(fun)
    
    spark.udf.register("hello", tb.eval(tree).asInstanceOf[Double => Double])
  }
}
