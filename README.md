sparklyudf: Scala UDF from R - Example
================

sparklyudf demonstrates how to build a
[sparklyr](http://github.com/rstudio/sparklyr) extension package that
uses custom Scala code which is compiled, deployed to Apache Spark and
registered as an UDF that can be used in SQL and
[dplyr](https://github.com/tidyverse/dplyr).

## Building

First build this package, then build its Spark 2.0 jars by running:

``` r
spec <- sparklyr::spark_default_compilation_spec()
spec <- Filter(function(e) e$spark_version >= "2.0.0", spec)
sparklyr::compile_package_jars()
```

then build the R package as usual.

This package contains an Scala-based UDF defined as:

``` scala
object Main {
  def register_hello(spark: SparkSession) = {
    spark.udf.register("hello", (name: String) => {
      "Hello, " + name + "! - From Scala"
    })
  }
}
```

## Getting Started

Connect and test this package as follows:

``` r
library(sparklyudf)
library(sparklyr)
library(dplyr)
```

    ## 
    ## Attaching package: 'dplyr'

    ## The following objects are masked from 'package:stats':
    ## 
    ##     filter, lag

    ## The following objects are masked from 'package:base':
    ## 
    ##     intersect, setdiff, setequal, union

``` r
sc <- spark_connect(master = "local")

sparklyudf_register(sc, "(x: Double) => x * 2")
```

    ## <jobj[16]>
    ##   org.apache.spark.sql.expressions.UserDefinedFunction
    ##   UserDefinedFunction(<function1>,DoubleType,Some(List(DoubleType)))

Now the Scala UDF `hello()` is registered and can be used as follows:

``` r
data.frame(name = "Javier") %>%
  copy_to(sc, .) %>%
  mutate(hello = hello(2.0))
```

    ## Error: org.apache.spark.SparkException: Job aborted due to stage failure: Task 0 in stage 5.0 failed 1 times, most recent failure: Lost task 0.0 in stage 5.0 (TID 5, localhost, executor driver): java.lang.ClassCastException: cannot assign instance of scala.collection.immutable.List$SerializationProxy to field org.apache.spark.rdd.RDD.org$apache$spark$rdd$RDD$$dependencies_ of type scala.collection.Seq in instance of org.apache.spark.rdd.MapPartitionsRDD
    ##  at java.io.ObjectStreamClass$FieldReflector.setObjFieldValues(ObjectStreamClass.java:2233)
    ##  at java.io.ObjectStreamClass.setObjFieldValues(ObjectStreamClass.java:1405)
    ##  at java.io.ObjectInputStream.defaultReadFields(ObjectInputStream.java:2284)
    ##  at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2202)
    ##  at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2060)
    ##  at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1567)
    ##  at java.io.ObjectInputStream.defaultReadFields(ObjectInputStream.java:2278)
    ##  at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2202)
    ##  at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2060)
    ##  at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1567)
    ##  at java.io.ObjectInputStream.readObject(ObjectInputStream.java:427)
    ##  at scala.collection.immutable.List$SerializationProxy.readObject(List.scala:490)
    ##  at sun.reflect.GeneratedMethodAccessor15.invoke(Unknown Source)
    ##  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    ##  at java.lang.reflect.Method.invoke(Method.java:498)
    ##  at java.io.ObjectStreamClass.invokeReadObject(ObjectStreamClass.java:1158)
    ##  at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2169)
    ##  at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2060)
    ##  at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1567)
    ##  at java.io.ObjectInputStream.defaultReadFields(ObjectInputStream.java:2278)
    ##  at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2202)
    ##  at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2060)
    ##  at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1567)
    ##  at java.io.ObjectInputStream.defaultReadFields(ObjectInputStream.java:2278)
    ##  at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2202)
    ##  at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2060)
    ##  at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1567)
    ##  at java.io.ObjectInputStream.readObject(ObjectInputStream.java:427)
    ##  at org.apache.spark.serializer.JavaDeserializationStream.readObject(JavaSerializer.scala:75)
    ##  at org.apache.spark.serializer.JavaSerializerInstance.deserialize(JavaSerializer.scala:114)
    ##  at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:83)
    ##  at org.apache.spark.scheduler.Task.run(Task.scala:121)
    ##  at org.apache.spark.executor.Executor$TaskRunner$$anonfun$10.apply(Executor.scala:402)
    ##  at org.apache.spark.util.Utils$.tryWithSafeFinally(Utils.scala:1360)
    ##  at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:408)
    ##  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    ##  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    ##  at java.lang.Thread.run(Thread.java:748)
    ## 
    ## Driver stacktrace:
    ##  at org.apache.spark.scheduler.DAGScheduler.org$apache$spark$scheduler$DAGScheduler$$failJobAndIndependentStages(DAGScheduler.scala:1887)
    ##  at org.apache.spark.scheduler.DAGScheduler$$anonfun$abortStage$1.apply(DAGScheduler.scala:1875)
    ##  at org.apache.spark.scheduler.DAGScheduler$$anonfun$abortStage$1.apply(DAGScheduler.scala:1874)
    ##  at scala.collection.mutable.ResizableArray$class.foreach(ResizableArray.scala:59)
    ##  at scala.collection.mutable.ArrayBuffer.foreach(ArrayBuffer.scala:48)
    ##  at org.apache.spark.scheduler.DAGScheduler.abortStage(DAGScheduler.scala:1874)
    ##  at org.apache.spark.scheduler.DAGScheduler$$anonfun$handleTaskSetFailed$1.apply(DAGScheduler.scala:926)
    ##  at org.apache.spark.scheduler.DAGScheduler$$anonfun$handleTaskSetFailed$1.apply(DAGScheduler.scala:926)
    ##  at scala.Option.foreach(Option.scala:257)
    ##  at org.apache.spark.scheduler.DAGScheduler.handleTaskSetFailed(DAGScheduler.scala:926)
    ##  at org.apache.spark.scheduler.DAGSchedulerEventProcessLoop.doOnReceive(DAGScheduler.scala:2108)
    ##  at org.apache.spark.scheduler.DAGSchedulerEventProcessLoop.onReceive(DAGScheduler.scala:2057)
    ##  at org.apache.spark.scheduler.DAGSchedulerEventProcessLoop.onReceive(DAGScheduler.scala:2046)
    ##  at org.apache.spark.util.EventLoop$$anon$1.run(EventLoop.scala:49)
    ##  at org.apache.spark.scheduler.DAGScheduler.runJob(DAGScheduler.scala:737)
    ##  at org.apache.spark.SparkContext.runJob(SparkContext.scala:2061)
    ##  at org.apache.spark.SparkContext.runJob(SparkContext.scala:2082)
    ##  at org.apache.spark.SparkContext.runJob(SparkContext.scala:2101)
    ##  at org.apache.spark.sql.execution.SparkPlan.executeTake(SparkPlan.scala:365)
    ##  at org.apache.spark.sql.execution.CollectLimitExec.executeCollect(limit.scala:38)
    ##  at org.apache.spark.sql.Dataset.org$apache$spark$sql$Dataset$$collectFromPlan(Dataset.scala:3384)
    ##  at org.apache.spark.sql.Dataset$$anonfun$collect$1.apply(Dataset.scala:2783)
    ##  at org.apache.spark.sql.Dataset$$anonfun$collect$1.apply(Dataset.scala:2783)
    ##  at org.apache.spark.sql.Dataset$$anonfun$53.apply(Dataset.scala:3365)
    ##  at org.apache.spark.sql.execution.SQLExecution$$anonfun$withNewExecutionId$1.apply(SQLExecution.scala:78)
    ##  at org.apache.spark.sql.execution.SQLExecution$.withSQLConfPropagated(SQLExecution.scala:125)
    ##  at org.apache.spark.sql.execution.SQLExecution$.withNewExecutionId(SQLExecution.scala:73)
    ##  at org.apache.spark.sql.Dataset.withAction(Dataset.scala:3364)
    ##  at org.apache.spark.sql.Dataset.collect(Dataset.scala:2783)
    ##  at sparklyr.Utils$.collect(utils.scala:204)
    ##  at sparklyr.Utils.collect(utils.scala)
    ##  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    ##  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    ##  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    ##  at java.lang.reflect.Method.invoke(Method.java:498)
    ##  at sparklyr.Invoke.invoke(invoke.scala:147)
    ##  at sparklyr.StreamHandler.handleMethodCall(stream.scala:123)
    ##  at sparklyr.StreamHandler.read(stream.scala:66)
    ##  at sparklyr.BackendHandler.channelRead0(handler.scala:51)
    ##  at sparklyr.BackendHandler.channelRead0(handler.scala:4)
    ##  at io.netty.channel.SimpleChannelInboundHandler.channelRead(SimpleChannelInboundHandler.java:105)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348)
    ##  at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340)
    ##  at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:102)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348)
    ##  at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340)
    ##  at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:310)
    ##  at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:284)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348)
    ##  at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340)
    ##  at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1359)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362)
    ##  at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348)
    ##  at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:935)
    ##  at io.net

``` r
spark_disconnect_all()
```

    ## [1] 1
