package io.github.thecristopher.lumen.home.actions

import io.github.thecristopher.lumen.apps.AppArt
import io.github.thecristopher.lumen.apps.AppRepository
import io.github.thecristopher.lumen.apps.DEFAULT_FAVORITES
import io.github.thecristopher.lumen.apps.DEFAULT_HIDDEN
import io.github.thecristopher.lumen.apps.HomeLayout
import io.github.thecristopher.lumen.apps.helpers.orderApps
import io.github.thecristopher.lumen.apps.helpers.splitHome
import io.github.thecristopher.lumen.shelf.ShelfRepository
import io.github.thecristopher.lumen.shelf.ShelfRow
import io.github.thecristopher.lumen.shelf.helpers.pickShelf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Reads the installed apps and lays them out with the default favorites and hidden list.
 * @param repository source of installed apps
 * @returns the dock and grid to draw
 */
suspend fun loadLayout(repository: AppRepository): HomeLayout = withContext(Dispatchers.IO) {
    splitHome(orderApps(repository.launchableApps(), DEFAULT_FAVORITES, DEFAULT_HIDDEN))
}

/**
 * Reads the TV provider once and picks the Top Shelf row of every dock app.
 * @param repository source of watch next and preview channel content
 * @param layout apps on screen, only the dock gets a shelf
 * @returns rows keyed by package name, apps with nothing published are left out
 */
suspend fun loadShelves(repository: ShelfRepository, layout: HomeLayout): Map<String, ShelfRow> =
    withContext(Dispatchers.IO) {
        val watchNext = repository.watchNext()
        val previews = repository.previews()
        layout.dock
            .mapNotNull { app -> pickShelf(app.packageName, watchNext, previews)?.let { app.packageName to it } }
            .toMap()
    }

/**
 * Loads every tile's art one app at a time, so tiles fill in as soon as theirs is ready.
 * @param repository source of banners and icons
 * @param layout apps on screen
 * @param into map keyed by package name that the tiles read from
 */
suspend fun loadArt(repository: AppRepository, layout: HomeLayout, into: MutableMap<String, AppArt>) {
    for (app in layout.dock + layout.grid) {
        val art = withContext(Dispatchers.IO) { repository.art(app) } ?: continue
        into[app.packageName] = art
    }
}
