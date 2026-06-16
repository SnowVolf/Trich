package ru.svolf.trich.uikit.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

fun Modifier.liquidGlassEffect(
    foregroundColor: Color,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.ui.graphics.RectangleShape,
    thickness: Float = 8f,
    intensity: Float = 10f,
    index: Float = 1.5f,
): Modifier = composed {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val density = LocalDensity.current

        val shaderString = remember {
            try {
                context.assets.open("liquid_glass.agsl").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                null
            }
        }

        if (shaderString != null) {
            val runtimeShader = remember(shaderString) { RuntimeShader(shaderString) }
            this.graphicsLayer {
                clip = true
                this.shape = shape

                val resolutionX = size.width
                val resolutionY = size.height
                val centerX = resolutionX / 2f
                val centerY = resolutionY / 2f
                val sizeX = resolutionX / 2f
                val sizeY = resolutionY / 2f

                var radiusLeftTop = size.height / 2f
                var radiusRightTop = size.height / 2f
                var radiusRightBottom = size.height / 2f
                var radiusLeftBottom = size.height / 2f

                val height = size.height
                if (radiusLeftTop + radiusLeftBottom > height) {
                    val a = radiusLeftTop / (radiusLeftTop + radiusLeftBottom)
                    radiusLeftTop = height * a
                    radiusLeftBottom = height * (1.0f - a)
                }
                if (radiusRightTop + radiusRightBottom > height) {
                    val a = radiusRightTop / (radiusRightTop + radiusRightBottom)
                    radiusRightTop = height * a
                    radiusRightBottom = height * (1.0f - a)
                }

                val a = foregroundColor.alpha
                val r = foregroundColor.red * a
                val g = foregroundColor.green * a
                val b = foregroundColor.blue * a

                runtimeShader.setFloatUniform("resolution", resolutionX, resolutionY)
                runtimeShader.setFloatUniform("center", centerX, centerY)
                runtimeShader.setFloatUniform("size", sizeX, sizeY)
                runtimeShader.setFloatUniform(
                    "radius",
                    radiusRightBottom,
                    radiusRightTop,
                    radiusLeftBottom,
                    radiusLeftTop
                )
                runtimeShader.setFloatUniform("thickness", thickness * density.density)
                runtimeShader.setFloatUniform("refract_intensity", intensity * density.density)
                runtimeShader.setFloatUniform("refract_index", index)
                runtimeShader.setFloatUniform("foreground_color_premultiplied", r, g, b, a)

                renderEffect = RenderEffect.createRuntimeShaderEffect(runtimeShader, "img")
                    .asComposeRenderEffect()
            }
        } else {
            this
        }
    } else {
        this
    }
}
