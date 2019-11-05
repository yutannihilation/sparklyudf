package sparklyudf

import org.apache.spark.sql.SparkSession;

object Main {
  def register_hello(spark: SparkSession, fun: String) = {
    spark.udf.register("hello", () => {
      "Hello, " + fun + "! - From Scala"
    })
  }
}
