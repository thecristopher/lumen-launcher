package io.github.thecristopher.lumen.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import io.github.thecristopher.lumen.R
import io.github.thecristopher.lumen.apps.LaunchableApp
import io.github.thecristopher.lumen.shelf.ShelfItem
import io.github.thecristopher.lumen.shelf.ShelfRow
import io.github.thecristopher.lumen.ui.theme.LumenType

/** Height of the area above the dock. */
val ShelfHeight = 300.dp

private val ShelfSide = 32.dp
private val CardGap = 14.dp

/**
 * The area above the dock. Shows what the focused dock app published (what you were watching, or its
 * preview channel), and names the app when it published nothing.
 * @param app the dock app the shelf belongs to, null before anything is focused
 * @param row that app's content, null when it has none
 * @param onOpen opens a card's content
 */
@Composable
fun TopShelf(app: LaunchableApp?, row: ShelfRow?, onOpen: (ShelfItem) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(ShelfHeight)
            .padding(bottom = 30.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        AnimatedContent(
            targetState = app to row,
            transitionSpec = {
                (fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 6 }) togetherWith fadeOut(tween(200))
            },
            label = "shelf",
        ) { (currentApp, currentRow) ->
            if (currentApp != null && currentRow != null) {
                ShelfCards(currentApp, currentRow, onOpen)
            } else {
                ShelfHero(currentApp, Modifier.padding(horizontal = ShelfSide))
            }
        }
    }
}

@Composable
private fun ShelfCards(app: LaunchableApp, row: ShelfRow, onOpen: (ShelfItem) -> Unit) {
    val label = row.channelName ?: stringResource(R.string.shelf_continue)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$label · ${app.label}", style = LumenType.ShelfLabel, modifier = Modifier.padding(horizontal = ShelfSide))
        // Vertical padding leaves room for the focused card to grow without being clipped.
        LazyRow(
            contentPadding = PaddingValues(horizontal = ShelfSide, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(CardGap),
        ) {
            items(row.items, key = { "${it.title}|${it.intentUri}" }) { item ->
                ShelfCard(item, onClick = { onOpen(item) })
            }
        }
    }
}

@Composable
private fun ShelfHero(app: LaunchableApp?, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(R.string.shelf_eyebrow).uppercase(), style = LumenType.Eyebrow)
        Text(app?.label ?: stringResource(R.string.app_name), style = LumenType.Hero)
        Text(
            stringResource(if (app == null) R.string.shelf_welcome else R.string.shelf_soon),
            style = LumenType.HeroLine,
        )
    }
}
