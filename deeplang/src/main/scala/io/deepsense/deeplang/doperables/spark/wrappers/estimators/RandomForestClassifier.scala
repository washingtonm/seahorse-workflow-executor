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

package io.deepsense.deeplang.doperables.spark.wrappers.estimators

import org.apache.spark.ml.classification.{RandomForestClassificationModel => SparkRandomForestClassificationModel, RandomForestClassifier => SparkRandomForestClassifier}

import io.deepsense.deeplang.doperables.SparkEstimatorWrapper
import io.deepsense.deeplang.doperables.spark.wrappers.models.{RandomForestClassificationModel, VanillaRandomForestClassificationModel}
import io.deepsense.deeplang.doperables.spark.wrappers.params.common._
import io.deepsense.deeplang.doperables.stringindexingwrapper.StringIndexingEstimatorWrapper
import io.deepsense.deeplang.params.Param

class RandomForestClassifier private (
    val vanillaRandomForestClassifier: VanillaRandomForestClassifier)
  extends StringIndexingEstimatorWrapper[
    SparkRandomForestClassificationModel,
    SparkRandomForestClassifier,
    VanillaRandomForestClassificationModel,
    RandomForestClassificationModel](vanillaRandomForestClassifier) {

  def this() = this(new VanillaRandomForestClassifier())
}

class VanillaRandomForestClassifier
  extends SparkEstimatorWrapper[
    SparkRandomForestClassificationModel,
    SparkRandomForestClassifier,
    VanillaRandomForestClassificationModel]
    with HasMaxDepthParam
    with HasMaxBinsParam
    with HasMinInstancePerNodeParam
    with HasMinInfoGainParam
    with HasMaxMemoryInMBParam
    with HasCacheNodeIdsParam
    with HasCheckpointIntervalParam
    with HasSubsamplingRateParam
    with HasSeedParam
    with HasNumTreesParam
    with HasFeatureSubsetStrategyParam
    with PredictorParams
    with HasLabelColumnParam
    with ProbabilisticClassifierParams
    with HasClassificationImpurityParam {

  override val params: Array[Param[_]] = declareParams(
    maxDepth,
    maxBins,
    minInstancesPerNode,
    minInfoGain,
    maxMemoryInMB,
    cacheNodeIds,
    checkpointInterval,
    impurity,
    subsamplingRate,
    seed,
    numTrees,
    featureSubsetStrategy,
    labelColumn,
    featuresColumn,
    probabilityColumn,
    rawPredictionColumn,
    predictionColumn
    // TODO Thresholds param
  )

  override protected def estimatorName: String = classOf[RandomForestClassifier].getSimpleName
}
