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

package io.deepsense.deeplang.params

import io.deepsense.deeplang.params.selections.{SingleColumnSelection, SingleColumnSelectionProtocol}
import io.deepsense.deeplang.params.selections.SingleColumnSelectionProtocol._

case class SingleColumnSelectorParam(
    name: String,
    description: String,
    portIndex: Int)
  extends AbstractColumnSelectorParam[SingleColumnSelection]
  with SingleColumnSelectionProtocol {

  override val parameterType = ParameterType.ColumnSelector
  override val isSingle = true

  override def replicate(name: String): SingleColumnSelectorParam = copy(name = name)
}
