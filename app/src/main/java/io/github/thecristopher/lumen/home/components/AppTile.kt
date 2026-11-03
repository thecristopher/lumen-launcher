package io.github.thecristopher.lumen.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.tv.material3.Text
import io.github.thecristopher.lumen.apps.AppArt
import io.github.thecristopher.lumen.apps.LaunchableApp
import io.github.thecristopher.lumen.home.helpers.iconTileGradient
import io.github.thecristopher.lumen.ui.theme.LumenColors
import io.github.thecristopher.lumen.ui.theme.LumenType

val TileWidth = 160.dp
val TileHeight = 96.dp
private val TileShape = RoundedCornerShape(10.dp)
private val IconShape = RoundedCornerShape(14.dp)
private val IconSize = 60.dp
private const val FOCUSED_SCALE = 1.12f
private const val SWAY_MS = 5_500
private const val TILT_X_DEGREES = 3f
private const val TILT_Y_DEGREES = 4f

/**
 * One app on the home screen. With focus it grows, lifts, sways slowly in 3D and catches a moving
 * shine, the way tvOS icons do.
 * @param app the app this tile opens
 * @param art its banner or icon, null while it loads
 * @param onFocused called when the tile gains focus
 * @param onClick called on OK or Enter
 */
@Composable
fun AppTile(
    app: LaunchableApp,
    art: AppArt?,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    LaunchedEffect(focused) { if (focused) onFocused() }

    val scale by animateFloatAsState(
        if (focused) FOCUSED_SCALE else 1f,
        spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "scale",
    )
    val lift by animateDpAsState(if (focused) 22.dp else 4.dp, label = "lift")
    val sway = if (focused) rememberSway() else 0f

    Column(
        modifier.zIndex(if (focused) 1f else 0f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier
                .size(TileWidth, TileHeight)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationX = sway * TILT_X_DEGREES
                    rotationY = -sway * TILT_Y_DEGREES
                    cameraDistance = 14f * density
                }
                .shadow(lift, TileShape)
                .clip(TileShape)
                .background(tileBrush(art))
                .clickable(interactionSource = interaction, indication = null, onClick = onClick)
                .shine(focused, sway),
            contentAlignment = Alignment.Center,
        ) {
            TileArt(app, art)
        }
        Text(
            app.label,
            style = LumenType.TileName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(if (focused) 1f else 0f),
        )
    }
}

/** Slow back and forth value between -1 and 1 that drives the tilt and the shine. */
@Composable
private fun rememberSway(): Float {
    val transition = rememberInfiniteTransition(label = "sway")
    val sway by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(SWAY_MS), RepeatMode.Reverse),
        label = "sway",
    )
    return sway
}

@Composable
private fun TileArt(app: LaunchableApp, art: AppArt?) {
    when {
        art == null -> Text(app.label, style = LumenType.TileFallback, maxLines = 2, overflow = TextOverflow.Ellipsis)
        art.wide -> Image(art.image, contentDescription = app.label, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        else -> Image(
            art.image,
            contentDescription = app.label,
            modifier = Modifier.size(IconSize).shadow(6.dp, IconShape).clip(IconShape),
        )
    }
}

private fun tileBrush(art: AppArt?): Brush = when {
    art == null || art.wide -> SolidColor(LumenColors.Tile)
    else -> Brush.verticalGradient(iconTileGradient(art.glow))
}

/** Soft light that slides across the tile while it has focus. */
private fun Modifier.shine(focused: Boolean, sway: Float): Modifier = drawWithContent {
    drawContent()
    if (!focused) return@drawWithContent
    drawRect(
        Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.38f), Color.Transparent),
            center = Offset(size.width * (0.5f + sway * 0.35f), 0f),
            radius = size.width * 0.9f,
        ),
        blendMode = BlendMode.Screen,
    )
}
