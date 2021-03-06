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

package io.deepsense.deeplang.params.wrappers.spark

import org.apache.spark.ml
import org.apache.spark.sql.types.StructType

import io.deepsense.deeplang.doperables.dataframe.DataFrameColumnsGetter
import io.deepsense.deeplang.params.ColumnSelectorParam
import io.deepsense.deeplang.params.selections.MultipleColumnSelection


class ColumnSelectorParamWrapper[P <: ml.param.Params](
    override val name: String,
    override val description: String,
    val sparkParamGetter: P => ml.param.StringArrayParam,
    override val portIndex: Int = 0)
  extends ColumnSelectorParam(name, description, portIndex)
  with SparkParamWrapper[P, Array[String], MultipleColumnSelection] {

  override def convert(value: MultipleColumnSelection)(schema: StructType): Array[String] =
    DataFrameColumnsGetter.getColumnNames(schema, value).toArray

  override def replicate(name: String): ColumnSelectorParamWrapper[P] =
    new ColumnSelectorParamWrapper[P](name, description, sparkParamGetter, portIndex)
}
