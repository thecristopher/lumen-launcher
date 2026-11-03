package io.github.thecristopher.lumen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import io.github.thecristopher.lumen.apps.AppArt
import io.github.thecristopher.lumen.apps.AppRepository
import io.github.thecristopher.lumen.apps.HomeLayout
import io.github.thecristopher.lumen.apps.LaunchableApp
import io.github.thecristopher.lumen.apps.helpers.DOCK_SIZE
import io.github.thecristopher.lumen.home.actions.loadArt
import io.github.thecristopher.lumen.home.actions.loadLayout
import io.github.thecristopher.lumen.home.actions.loadShelves
import io.github.thecristopher.lumen.shelf.ShelfItem
import io.github.thecristopher.lumen.shelf.ShelfRepository
import io.github.thecristopher.lumen.shelf.ShelfRow
import io.github.thecristopher.lumen.home.components.AppTile
import io.github.thecristopher.lumen.home.components.Backdrop
import io.github.thecristopher.lumen.home.components.Clock
import io.github.thecristopher.lumen.home.components.NeutralGlow
import io.github.thecristopher.lumen.home.components.TopShelf
import io.github.thecristopher.lumen.home.helpers.KeepInViewSpec

private val SidePadding = 32.dp
private val TileGap = 24.dp
private val RowGap = 18.dp

/**
 * The home screen: backdrop, clock, Top Shelf, the dock and the grid of every other app.
 * @param repository source of apps and artwork
 * @param shelfRepository source of the Top Shelf content
 * @param homePresses grows on every Home press, each change sends focus back to the first app
 * @param refreshes grows every time the screen comes back, each change rereads the Top Shelf
 * @param onLaunch opens the chosen app
 * @param onOpen opens a Top Shelf card's content
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    repository: AppRepository,
    shelfRepository: ShelfRepository,
    homePresses: Int,
    refreshes: Int,
    onLaunch: (LaunchableApp) -> Unit,
    onOpen: (ShelfItem) -> Unit,
) {
    val layout by produceState(HomeLayout.EMPTY, repository) { value = loadLayout(repository) }
    val art = remember { mutableStateMapOf<String, AppArt>() }
    LaunchedEffect(layout) { loadArt(repository, layout, art) }
    val shelves by produceState(emptyMap<String, ShelfRow>(), layout, refreshes) {
        value = loadShelves(shelfRepository, layout)
    }

    var focused by remember { mutableStateOf<LaunchableApp?>(null) }
    var shelfApp by remember { mutableStateOf<LaunchableApp?>(null) }
    val onFocused: (LaunchableApp) -> Unit = { app ->
        focused = app
        if (app in layout.dock) shelfApp = app
    }

    val listState = rememberLazyListState()
    val firstTile = remember { FocusRequester() }
    LaunchedEffect(layout, homePresses) {
        if (layout.dock.isEmpty()) return@LaunchedEffect
        listState.scrollToItem(0)
        // Wait one frame so the tile is attached before asking for focus.
        withFrameNanos {}
        runCatching { firstTile.requestFocus() }
    }

    // A launcher has nowhere to go back to.
    BackHandler {}

    Box(Modifier.fillMaxSize()) {
        Backdrop(glow = focused?.let { art[it.packageName]?.glow } ?: NeutralGlow)
        CompositionLocalProvider(LocalBringIntoViewSpec provides KeepInViewSpec) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(RowGap),
            ) {
                item { TopShelf(shelfApp, shelves[shelfApp?.packageName], onOpen) }
                items(rowsOf(layout)) { row ->
                    TileRow(row, art, layout.dock.firstOrNull(), firstTile, onFocused, onLaunch)
                }
            }
        }
        Clock(Modifier.align(Alignment.TopEnd).padding(top = 22.dp, end = SidePadding))
    }
}

@Composable
private fun TileRow(
    apps: List<LaunchableApp>,
    art: Map<String, AppArt>,
    firstApp: LaunchableApp?,
    firstTile: FocusRequester,
    onFocused: (LaunchableApp) -> Unit,
    onLaunch: (LaunchableApp) -> Unit,
) {
    Row(
        Modifier.padding(horizontal = SidePadding),
        horizontalArrangement = Arrangement.spacedBy(TileGap),
    ) {
        apps.forEach { app ->
            AppTile(
                app = app,
                art = art[app.packageName],
                onFocused = { onFocused(app) },
                onClick = { onLaunch(app) },
                modifier = if (app == firstApp) Modifier.focusRequester(firstTile) else Modifier,
            )
        }
    }
}

/** The dock first, then the grid in rows as wide as the dock. */
private fun rowsOf(layout: HomeLayout): List<List<LaunchableApp>> =
    listOf(layout.dock).filter { it.isNotEmpty() } + layout.grid.chunked(DOCK_SIZE)
