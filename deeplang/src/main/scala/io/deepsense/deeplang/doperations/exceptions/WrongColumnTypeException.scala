/**
 * Copyright 2015, CodiLime Inc.
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

package io.deepsense.deeplang.doperations.exceptions

import io.deepsense.deeplang.parameters.ColumnType.ColumnType


case class WrongColumnTypeException(
    columnName: String,
    actualType: ColumnType,
    expectedTypes: ColumnType*)
  extends DOperationExecutionException(
    s"Column '$columnName' has type $actualType instead of expected ${expectedTypes.mkString}",
    None)
