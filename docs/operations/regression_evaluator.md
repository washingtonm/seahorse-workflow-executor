---
layout: documentation
displayTitle: Regression Evaluator
title: Regression Evaluator
description: Regression Evaluator
usesMathJax: true
includeOperationsMenu: true
---
Creates a regression evaluator.

This operation is ported from Spark ML. For more details, see: <a target="_blank" href="http://spark.apache.org/docs/1.6.0/api/scala/index.html#org.apache.spark.ml.evaluation.RegressionEvaluator">org.apache.spark.ml.evaluation.RegressionEvaluator documentation</a>.

**Since**: Seahorse 1.0.0

## Input

This operation does not take any input.

## Output


<table>
<thead>
<tr>
<th style="width:15%">Port</th>
<th style="width:15%">Type Qualifier</th>
<th style="width:70%">Description</th>
</tr>
</thead>
<tbody>
    <tr><td><code>0</code></td><td><code><a href="../classes/evaluator.html">Evaluator</a></code></td><td>Evaluator that can be used in <a href="evaluate.html">Evaluate</a> operation</td></tr>
</tbody>
</table>


## Parameters


<table class="table">
<thead>
<tr>
<th style="width:15%">Name</th>
<th style="width:15%">Type</th>
<th style="width:70%">Description</th>
</tr>
</thead>
<tbody>

<tr>
<td><code>metric</code></td>
<td><code><a href="../parameters.html#single_choice">SingleChoice</a></code></td>
<td>Metric used in evaluation. Possible values: <code>["mse", "rmse", "r2", "mae"]</code></td>
</tr>

<tr>
<td><code>prediction column</code></td>
<td><code><a href="../parameters.html#single_column_selector">SingleColumnSelector</a></code></td>
<td>Prediction column.</td>
</tr>

<tr>
<td><code>label column</code></td>
<td><code><a href="../parameters.html#single_column_selector">SingleColumnSelector</a></code></td>
<td>Label column for model fitting.</td>
</tr>

</tbody>
</table>
