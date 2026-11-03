package io.github.thecristopher.lumen.home.helpers

/** Position of the dock in the home list, the Top Shelf sits above it at 0. */
const val DOCK_ITEM = 1

/**
 * Which list item the home screen scrolls to when focus lands on a row. The Top Shelf and the dock
 * share one still screen; any row below them moves to the top, so the shelf leaves the screen whole
 * instead of being cut in half.
 * @param focusedItem index of the list item that holds focus
 * @returns index of the item to put at the top of the screen
 */
fun scrollAnchor(focusedItem: Int): Int = if (focusedItem <= DOCK_ITEM) 0 else focusedItem
