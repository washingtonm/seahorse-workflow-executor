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

package io.deepsense.deeplang.doperables.spark.wrappers.models

import org.apache.spark.ml.feature.{MinMaxScaler => SparkMinMaxScaler, MinMaxScalerModel => SparkMinMaxScalerModel}

import io.deepsense.deeplang.ExecutionContext
import io.deepsense.deeplang.doperables.SparkSingleColumnModelWrapper
import io.deepsense.deeplang.doperables.report.CommonTablesGenerators.SparkSummaryEntry
import io.deepsense.deeplang.doperables.report.{CommonTablesGenerators, Report}
import io.deepsense.deeplang.doperables.serialization.SerializableSparkModel
import io.deepsense.deeplang.doperables.spark.wrappers.params.common.MinMaxParams
import io.deepsense.deeplang.params.Param

class MinMaxScalerModel
  extends SparkSingleColumnModelWrapper[SparkMinMaxScalerModel, SparkMinMaxScaler]
  with MinMaxParams {

  override def convertInputNumericToVector: Boolean = true
  override def convertOutputVectorToDouble: Boolean = true

  override protected def getSpecificParams: Array[Param[_]] = Array(min, max)

  override def report: Report = {
    val summary =
      List(
        SparkSummaryEntry(
          name = "original min",
          value = sparkModel.originalMin,
          description = "Minimal value for each original column during fitting."),
        SparkSummaryEntry(
          name = "original max",
          value = sparkModel.originalMax,
          description = "Maximum value for each original column during fitting."))

    super.report
      .withAdditionalTable(CommonTablesGenerators.modelSummary(summary))
  }

  override protected def loadModel(
      ctx: ExecutionContext,
      path: String): SerializableSparkModel[SparkMinMaxScalerModel] = {
    new SerializableSparkModel(SparkMinMaxScalerModel.load(path))
  }
}
