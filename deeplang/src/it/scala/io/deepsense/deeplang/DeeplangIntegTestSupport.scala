/**
 * Copyright 2015, deepsense.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.deepsense.deeplang

import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{Matchers, BeforeAndAfterAll}
import org.scalatest.mockito.MockitoSugar._

import io.deepsense.commons.models.Id
import io.deepsense.commons.spark.sql.UserDefinedFunctions
import io.deepsense.deeplang.OperationExecutionDispatcher.Result
import io.deepsense.deeplang.catalogs.doperable.DOperableCatalog
import io.deepsense.deeplang.doperables.dataframe.{DataFrame, DataFrameBuilder}
import io.deepsense.deeplang.inference.InferContext
import io.deepsense.deeplang.utils.DataFrameMatchers


/**
 * Adds features to facilitate integration testing using Spark
 */
trait DeeplangIntegTestSupport extends UnitSpec with BeforeAndAfterAll {

  var commonExecutionContext: CommonExecutionContext = _
  implicit var executionContext: ExecutionContext = _

  val sparkConf: SparkConf = DeeplangIntegTestSupport.sparkConf
  val sparkContext: SparkContext = DeeplangIntegTestSupport.sparkContext
  val sparkSession: SparkSession = DeeplangIntegTestSupport.sparkSession

  val dOperableCatalog = {
    val catalog = new DOperableCatalog
    CatalogRecorder.registerDOperables(catalog)
    catalog
  }

  override def beforeAll(): Unit = {
    commonExecutionContext = prepareCommonExecutionContext()
    executionContext = prepareExecutionContext()
  }

  protected def prepareCommonExecutionContext(): CommonExecutionContext = {
    val inferContext = InferContext(
      DataFrameBuilder(sparkSession),
      "testTenantId",
      dOperableCatalog,
      mock[InnerWorkflowParser])

    new MockedCommonExecutionContext(
      sparkContext,
      sparkSession,
      inferContext,
      LocalFileSystemClient(),
      "testTenantId",
      mock[InnerWorkflowExecutor],
      mock[DataFrameStorage],
      mock[CustomCodeExecutionProvider])
  }

  protected def prepareExecutionContext(): ExecutionContext = {
    val inferContext = InferContext(
      DataFrameBuilder(sparkSession),
      "testTenantId",
      dOperableCatalog,
      mock[InnerWorkflowParser])

    new MockedExecutionContext(
      sparkContext,
      sparkSession,
      inferContext,
      LocalFileSystemClient(),
      "testTenantId",
      mock[InnerWorkflowExecutor],
      mock[ContextualDataFrameStorage],
      new MockedContextualCodeExecutor)
  }

  protected def createDataFrame(rows: Seq[Row], schema: StructType): DataFrame = {
    val rdd: RDD[Row] = sparkContext.parallelize(rows)
    val sparkDataFrame = sparkSession.createDataFrame(rdd, schema)
    DataFrame.fromSparkDataFrame(sparkDataFrame)
  }

  def executeOperation(op: DOperation, dfs: DataFrame*): DataFrame =
    op.execute(executionContext)(dfs.toVector).head.asInstanceOf[DataFrame]

  def createDir(path: String): Unit = {
    new java.io.File(path + "/id").getParentFile.mkdirs()
  }

  def createDataFrame[T <: Product : TypeTag : ClassTag](seq: Seq[T]): DataFrame = {
    DataFrame.fromSparkDataFrame(
      sparkSession.createDataFrame(sparkContext.parallelize(seq)))
  }

}

object DeeplangIntegTestSupport extends UnitSpec with DataFrameMatchers {
  val sparkConf: SparkConf = new SparkConf()
    .setMaster("local[4]")
    .setAppName("TestApp")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .registerKryoClasses(Array())
  val sparkContext: SparkContext = new SparkContext(sparkConf)
  val sparkSession: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

  UserDefinedFunctions.registerFunctions(sparkSession.udf)
}

private class MockedCommonExecutionContext(
    override val sparkContext: SparkContext,
    override val sparkSession: SparkSession,
    override val inferContext: InferContext,
    override val fsClient: FileSystemClient,
    override val tenantId: String,
    override val innerWorkflowExecutor: InnerWorkflowExecutor,
    override val dataFrameStorage: DataFrameStorage,
    override val customCodeExecutionProvider: CustomCodeExecutionProvider)
  extends CommonExecutionContext(
    sparkContext,
    sparkSession,
    inferContext,
    fsClient,
    "/tmp",
    tenantId,
    innerWorkflowExecutor,
    dataFrameStorage,
    customCodeExecutionProvider) {

  override def createExecutionContext(workflowId: Id, nodeId: Id): ExecutionContext =
    new MockedExecutionContext(sparkContext,
      sparkSession,
      inferContext,
      fsClient,
      tenantId,
      innerWorkflowExecutor,
      mock[ContextualDataFrameStorage],
      new MockedContextualCodeExecutor)
}

// TODO Unnecessary intermediate object. Remove.
private class MockedExecutionContext(
    override val sparkContext: SparkContext,
    override val sparkSession: SparkSession,
    override val inferContext: InferContext,
    override val fsClient: FileSystemClient,
    override val tenantId: String,
    override val innerWorkflowExecutor: InnerWorkflowExecutor,
    override val dataFrameStorage: ContextualDataFrameStorage,
    override val customCodeExecutor: ContextualCustomCodeExecutor)
  extends ExecutionContext(
    sparkContext,
    sparkSession,
    inferContext,
    fsClient,
    "/tmp",
    tenantId,
    innerWorkflowExecutor,
    dataFrameStorage,
    customCodeExecutor)

private class MockedCodeExecutor extends CustomCodeExecutor {

  override def isValid(code: String): Boolean = true

  override def run(workflowId: String, nodeId: String, code: String): Unit = ()
}

private class MockedCustomCodeExecutionProvider
  extends CustomCodeExecutionProvider(
    new MockedCodeExecutor, new MockedCodeExecutor, new MockedCustomOperationExecutor)

private class MockedContextualCodeExecutor
  extends ContextualCustomCodeExecutor(
    new MockedCustomCodeExecutionProvider, Id.randomId, Id.randomId)

private class MockedCustomOperationExecutor
  extends OperationExecutionDispatcher {
  override def executionStarted(workflowId: Id, nodeId: Id): Future[Result] =
    Future.successful(Right(()))
}
