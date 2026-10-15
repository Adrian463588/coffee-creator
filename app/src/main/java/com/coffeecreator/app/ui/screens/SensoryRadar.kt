package com.coffeecreator.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.core.model.SensoryEstimate
import com.coffeecreator.app.core.model.SensoryObservation
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Application-owned ordinal comparison; no proprietary flavor-wheel artwork. */
@Composable
internal fun SensoryRadar(observed: SensoryObservation?, estimated: SensoryEstimate?) {
    val axes = listOf("Aroma", "Acidity", "Sweetness", "Bitterness", "Body", "Clarity", "Finish", "Balance")
    val actual = observed?.let { listOf(it.aroma, it.acidity, it.sweetness, it.bitterness, it.body, it.clarity, it.finish, it.balance) }
    val prediction = estimated?.let {
        listOf(
            it.aroma,
            it.acidity,
            it.sweetness,
            it.bitterness,
            it.body,
            it.clarity,
            it.finish,
            it.balance
        )
    }
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val outline = MaterialTheme.colorScheme.outline
    Column {
        Text("Solid: recorded observation · dashed: uncalibrated estimate")
        Text(
            axes.mapIndexed { index, label ->
                "$label: observed ${actual?.get(index) ?: "absent"}, estimated ${prediction?.get(index) ?: "absent"}"
            }
                .joinToString(" · "),
            style = MaterialTheme.typography.bodySmall
        )
        Canvas(
            Modifier.fillMaxWidth().height(240.dp).semantics {
                contentDescription = "Ordinal sensory comparison. ${axes.joinToString()}. ${actual ?: "No recorded observation"}; " +
                    "estimated ${prediction ?: "absent"}. Estimated intensities are uncalibrated, not measured."
            }
        ) {
            val radius = size.minDimension * 0.42f
            fun point(index: Int, fraction: Float): Offset {
                val angle = index * 2 * PI / axes.size - PI / 2
                return center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * radius * fraction
            }
            axes.indices.forEach { drawLine(outline, center, point(it, 1f), strokeWidth = 1f) }
            listOf(actual to primary, prediction to tertiary).forEachIndexed { index, (values, color) ->
                if (values != null) {
                    val path = Path()
                    values.forEachIndexed { axis, score ->
                        val position = point(axis, score / 10f)
                        if (axis == 0) path.moveTo(position.x, position.y) else path.lineTo(position.x, position.y)
                    }
                    path.close()
                    drawPath(
                        path,
                        color,
                        style = Stroke(
                            width = 3f,
                            pathEffect = if (index == 1) PathEffect.dashPathEffect(floatArrayOf(12f, 8f)) else null
                        )
                    )
                }
            }
        }
    }
}
