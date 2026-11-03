package io.github.thecristopher.lumen.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import io.github.thecristopher.lumen.R
import io.github.thecristopher.lumen.apps.LaunchableApp
import io.github.thecristopher.lumen.home.helpers.ManualScrollSpec
import io.github.thecristopher.lumen.home.helpers.windowStart
import io.github.thecristopher.lumen.shelf.ShelfItem
import io.github.thecristopher.lumen.shelf.ShelfRow
import io.github.thecristopher.lumen.ui.theme.LumenType

/** Height of the area above the dock. */
val ShelfHeight = 300.dp

private val ShelfSide = 32.dp
private val CardGap = 14.dp

// A focused card grows 10%, about 11dp per side at 213dp wide.
private val CardGrowRoom = 12.dp

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
            .padding(bottom = 6.dp),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShelfCards(app: LaunchableApp, row: ShelfRow, onOpen: (ShelfItem) -> Unit) {
    val label = row.channelName ?: stringResource(R.string.shelf_continue)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("$label · ${app.label}", style = LumenType.ShelfLabel, modifier = Modifier.padding(horizontal = ShelfSide))
        // The row is only as wide as the dock plus room for a focused card to grow, and it moves by
        // whole cards, so no card ever shows cut at an edge.
        val cards = rememberLazyListState()
        var focusedCard by remember { mutableIntStateOf(0) }
        LaunchedEffect(focusedCard) {
            cards.animateScrollToItem(windowStart(cards.firstVisibleItemIndex, focusedCard))
        }
        CompositionLocalProvider(LocalBringIntoViewSpec provides ManualScrollSpec) {
            LazyRow(
                state = cards,
                modifier = Modifier.padding(horizontal = ShelfSide - CardGrowRoom).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = CardGrowRoom, vertical = CardGrowRoom),
                horizontalArrangement = Arrangement.spacedBy(CardGap),
            ) {
                itemsIndexed(row.items, key = { _, it -> "${it.title}|${it.intentUri}" }) { index, item ->
                    ShelfCard(item, onFocused = { focusedCard = index }, onClick = { onOpen(item) })
                }
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
