package io.github.thecristopher.lumen.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import io.github.thecristopher.lumen.ui.theme.LumenColors

/** Glow used before any app has focus. */
val NeutralGlow = Color(0xFF3A3D48)

private const val GLOW_FADE_MS = 900

/**
 * Full screen backdrop: two soft blooms of the focused app's color over ink, darkening toward the bottom
 * so the tiles stay readable.
 * @param glow color of the focused app, crossfaded on every change
 */
@Composable
fun Backdrop(glow: Color, modifier: Modifier = Modifier) {
    val color by animateColorAsState(glow, tween(GLOW_FADE_MS), label = "glow")
    Box(
        modifier
            .fillMaxSize()
            .background(LumenColors.Ink)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(size.width * 0.78f, size.height * 0.16f),
                        radius = size.width * 0.55f,
                    ),
                )
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(size.width * 0.14f, size.height * 0.9f),
                        radius = size.width * 0.5f,
                    ),
                )
                drawRect(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.55f to Color.Black.copy(alpha = 0.35f),
                        1f to Color.Black.copy(alpha = 0.85f),
                    ),
                )
            },
    )
}
