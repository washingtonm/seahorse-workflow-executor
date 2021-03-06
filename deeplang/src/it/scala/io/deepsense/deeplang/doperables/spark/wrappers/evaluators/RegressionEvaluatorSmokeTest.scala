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

package io.deepsense.deeplang.doperables.spark.wrappers.evaluators

import io.deepsense.deeplang.doperables.AbstractEvaluatorSmokeTest
import io.deepsense.deeplang.params.ParamPair
import io.deepsense.deeplang.params.selections.NameSingleColumnSelection

class RegressionEvaluatorSmokeTest extends AbstractEvaluatorSmokeTest {

  override def className: String = "RegressionEvaluator"

  override val evaluator: RegressionEvaluator = new RegressionEvaluator()

  override val evaluatorParams: Seq[ParamPair[_]] = Seq(
    evaluator.metricName -> RegressionEvaluator.Rmse(),
    evaluator.predictionColumn -> NameSingleColumnSelection("prediction"),
    evaluator.labelColumn -> NameSingleColumnSelection("label")
  )
}
