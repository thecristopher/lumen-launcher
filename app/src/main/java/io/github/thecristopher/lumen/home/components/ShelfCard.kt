package io.github.thecristopher.lumen.home.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import io.github.thecristopher.lumen.shelf.ShelfItem
import io.github.thecristopher.lumen.ui.theme.LumenColors
import io.github.thecristopher.lumen.ui.theme.LumenType

// Four cards fill the dock's width exactly: 4 x 213 + 3 gaps of 14 = 894.
val CardWidth = 213.dp
val CardHeight = 120.dp
private val CardShape = RoundedCornerShape(8.dp)
private const val FOCUSED_SCALE = 1.1f

/**
 * A Top Shelf card: the poster an app published, with its title over a scrim once focused,
 * so the artwork speaks for itself until you land on it.
 * @param item the content this card opens
 * @param onFocused called when the card gains focus
 * @param onClick called on OK or Enter
 */
@Composable
fun ShelfCard(item: ShelfItem, onFocused: () -> Unit, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    LaunchedEffect(focused) { if (focused) onFocused() }
    val scale by animateFloatAsState(
        if (focused) FOCUSED_SCALE else 1f,
        spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "scale",
    )
    val lift by animateDpAsState(if (focused) 18.dp else 3.dp, label = "lift")
    val showText = focused || item.posterUri == null

    Box(
        modifier
            .zIndex(if (focused) 1f else 0f)
            .size(CardWidth, CardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(lift, CardShape)
            .clip(CardShape)
            .background(LumenColors.Tile)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        AsyncImage(
            model = item.posterUri,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        if (showText) CardCaption(item, Modifier.align(Alignment.BottomStart))
    }
}

@Composable
private fun CardCaption(item: ShelfItem, modifier: Modifier) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0.4f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.8f))),
    )
    Column(modifier.padding(10.dp)) {
        Text(item.title, style = LumenType.CardTitle, maxLines = 2, overflow = TextOverflow.Ellipsis)
        item.subtitle?.let { Text(it, style = LumenType.CardSubtitle, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
}
