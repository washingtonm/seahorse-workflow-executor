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

import java.lang.reflect.Constructor

import scala.reflect.runtime.universe._

import spray.json._

import io.deepsense.deeplang.TypeUtils
import io.deepsense.deeplang.params.exceptions.NoArgumentConstructorRequiredException

case class ParamsSequence[T <: Params](
    name: String,
    description: String)
    (implicit tag: TypeTag[T])
  extends Param[Seq[T]] {

  val parameterType = ParameterType.Multiplier

  override def valueToJson(value: Seq[T]): JsValue = {
    val cells = for (params <- value) yield params.paramValuesToJson
    JsArray(cells: _*)
  }

  private val constructor: Constructor[_] = TypeUtils.constructorForType(tag.tpe).getOrElse {
    throw NoArgumentConstructorRequiredException(tag.tpe.typeSymbol.asClass.name.decoded)
  }

  private def innerParamsInstance: T = constructor.newInstance().asInstanceOf[T]

  override def valueFromJson(jsValue: JsValue): Seq[T] = jsValue match {
    case JsArray(vector) =>
      for (innerJsValue <- vector) yield {
        innerParamsInstance.setParamsFromJson(innerJsValue)
      }
    case _ => throw new DeserializationException(s"Cannot fill parameters sequence" +
      s"with $jsValue: array expected.")
  }

  override def extraJsFields: Map[String, JsValue] = Map(
    "values" -> innerParamsInstance.paramsToJson
  )

  override def replicate(name: String): ParamsSequence[T] = copy(name = name)
}
