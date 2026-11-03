package io.github.thecristopher.lumen.shelf.helpers

import io.github.thecristopher.lumen.shelf.ShelfItem
import io.github.thecristopher.lumen.shelf.ShelfRow

/** Most cards the Top Shelf shows for one app. */
const val SHELF_MAX_ITEMS = 10

/**
 * Picks what the Top Shelf shows for an app: what you were watching first, most recent on the left,
 * and the app's first preview channel when it has no watch next entries.
 * @param packageName the app with focus in the dock
 * @param watchNext every watch next entry on the device
 * @param previews every preview channel item on the device, channels in publish order
 * @returns the row to draw, or null when the app published nothing
 */
fun pickShelf(packageName: String, watchNext: List<ShelfItem>, previews: List<ShelfItem>): ShelfRow? {
    val watching = watchNext
        .filter { it.packageName == packageName }
        .sortedByDescending { it.lastEngagement }
        .distinctBy { it.title }
        .take(SHELF_MAX_ITEMS)
    if (watching.isNotEmpty()) return ShelfRow(channelName = null, items = watching)

    val firstChannel = previews.firstOrNull { it.packageName == packageName }?.channelName
    val channelItems = previews
        .filter { it.packageName == packageName && it.channelName == firstChannel }
        .take(SHELF_MAX_ITEMS)
    return channelItems.takeIf { it.isNotEmpty() }?.let { ShelfRow(firstChannel, it) }
}
