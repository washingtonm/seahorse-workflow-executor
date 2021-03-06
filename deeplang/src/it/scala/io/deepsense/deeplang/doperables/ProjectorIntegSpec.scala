/**
 * Copyright 2016, deepsense.io
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

package io.deepsense.deeplang.doperables

import java.sql.Timestamp

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.joda.time.DateTime
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import io.deepsense.deeplang._
import io.deepsense.deeplang.doperables.Projector.ColumnProjection
import io.deepsense.deeplang.doperables.Projector.RenameColumnChoice.Yes
import io.deepsense.deeplang.doperables.dataframe.DataFrame
import io.deepsense.deeplang.doperables.spark.wrappers.transformers.TransformerSerialization
import io.deepsense.deeplang.doperations.exceptions.{ColumnDoesNotExistException, DuplicatedColumnsException}
import io.deepsense.deeplang.params.selections._

class ProjectorIntegSpec
  extends DeeplangIntegTestSupport
  with GeneratorDrivenPropertyChecks
  with Matchers
  with TransformerSerialization {

  import DeeplangIntegTestSupport._
  import TransformerSerialization._

  val columns = Seq(
    StructField("c", IntegerType),
    StructField("b", StringType),
    StructField("a", DoubleType),
    StructField("x", TimestampType),
    StructField("z", BooleanType))

  def schema: StructType = StructType(columns)

  //         "c"/0  "b"/1   "a"/2 "x"/3                                  "z"/4
  val row1 = Seq(1, "str1", 10.0, new Timestamp(DateTime.now.getMillis), true)
  val row2 = Seq(2, "str2", 20.0, new Timestamp(DateTime.now.getMillis), false)
  val row3 = Seq(3, "str3", 30.0, new Timestamp(DateTime.now.getMillis), false)
  val data = Seq(row1, row2, row3)
  val dataFrame = createDataFrame(data.map(Row.fromSeq), schema)

  "Projector" should {
    val expectedSchema = StructType(Seq(
      StructField("a", DoubleType),
      StructField("renamed_a", DoubleType)))
    val transformer = new Projector().setProjectionColumns(Seq(
      ColumnProjection().setOriginalColumn(NameSingleColumnSelection("a")),
      ColumnProjection().setOriginalColumn(NameSingleColumnSelection("a"))
        .setRenameColumn(new Yes().setColumnName("renamed_a"))
    ))

    "select correctly the same column multiple times" in {
      val projected = transformer._transform(executionContext, dataFrame)
      val expectedData = data.map(r => Seq(r(2), r(2)))
      val expectedDataFrame = createDataFrame(expectedData.map(Row.fromSeq), expectedSchema)
      assertDataFramesEqual(projected, expectedDataFrame)
      val projectedBySerializedTransformer = projectedUsingSerializedTransformer(transformer)
      assertDataFramesEqual(projected, projectedBySerializedTransformer)
    }
    "infer correct schema" in {
      val filteredSchema = transformer._transformSchema(schema)
      filteredSchema shouldBe Some(expectedSchema)
    }
   "throw an exception" when {
      "the columns selected by name does not exist" when {
        val transformer = new Projector().setProjectionColumns(Seq(
          ColumnProjection().setOriginalColumn(NameSingleColumnSelection("thisColumnDoesNotExist"))
        ))
        "transforming a DataFrame" in {
          intercept[ColumnDoesNotExistException] {
            transformer._transform(executionContext, dataFrame)
          }
        }
        "transforming a schema" in {
          intercept[ColumnDoesNotExistException] {
            transformer._transformSchema(schema)
          }
        }
      }
      "the columns selected by index does not exist" when {
        val transformer = new Projector().setProjectionColumns(Seq(
          ColumnProjection().setOriginalColumn(IndexSingleColumnSelection(1000))
        ))
        "transforming a DataFrame" in {
          intercept[ColumnDoesNotExistException] {
            transformer._transform(executionContext, dataFrame)
          }
        }
        "transforming a schema" in {
          intercept[ColumnDoesNotExistException] {
            transformer._transformSchema(schema)
          }
        }
      }
     "the output DataFrame has duplicated columns" when {
       val transformer = new Projector().setProjectionColumns(Seq(
         ColumnProjection().setOriginalColumn(NameSingleColumnSelection("a"))
           .setRenameColumn(new Yes().setColumnName("duplicatedName")),
         ColumnProjection().setOriginalColumn(NameSingleColumnSelection("b"))
           .setRenameColumn(new Yes().setColumnName("duplicatedName"))
       ))
       "transforming a DataFrame" in {
         intercept[DuplicatedColumnsException] {
           transformer._transform(executionContext, dataFrame)
         }
       }
     }
    }
  }
  it when {
    "selection is empty" should {
      val emptyProjector = new Projector().setProjectionColumns(Seq())
      "produce an empty DataFrame" in {
        val emptyDataFrame = emptyProjector._transform(executionContext, dataFrame)
        emptyDataFrame.sparkDataFrame.collectAsList() shouldBe empty
      }
      "produce an empty schema" in {
        val Some(inferredSchema) = emptyProjector._transformSchema(schema)
        inferredSchema.fields shouldBe empty
      }
    }
  }

  private def projectedUsingSerializedTransformer(transformer: Transformer): DataFrame = {
    transformer.loadSerializedTransformer(tempDir)._transform(executionContext, dataFrame)
  }
}
