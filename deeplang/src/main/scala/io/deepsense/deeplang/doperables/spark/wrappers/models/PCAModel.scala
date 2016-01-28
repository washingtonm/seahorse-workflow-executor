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

import org.apache.spark.ml
import org.apache.spark.ml.feature.{PCA => SparkPCA, PCAModel => SparkPCAModel}

import io.deepsense.deeplang.doperables.SparkModelWrapper
import io.deepsense.deeplang.doperables.report.{CommonTablesGenerators, Report}
import io.deepsense.deeplang.doperables.spark.wrappers.params.common.{HasInputColumn, HasOutputColumn}
import io.deepsense.deeplang.params.Param
import io.deepsense.deeplang.params.validators.RangeValidator
import io.deepsense.deeplang.params.wrappers.spark.IntParamWrapper

class PCAModel
  extends SparkModelWrapper[SparkPCAModel, SparkPCA]
  with HasInputColumn
  with HasOutputColumn {

  val k = new IntParamWrapper[ml.param.Params { val k: ml.param.IntParam }](
    name = "k",
    description = "Number of principal components.",
    sparkParamGetter = _.k,
    validator = RangeValidator.positiveIntegers)

  override val params: Array[Param[_]] = declareParams(k, inputColumn, outputColumn)

  override def report: Report = {
    super.report
      .withAdditionalTable(CommonTablesGenerators.denseMatrix(model.pc))
  }
}
