package io.github.thecristopher.lumen.home.helpers

/** How many Top Shelf cards fit across the screen. */
const val VISIBLE_CARDS = 4

/**
 * First card of the visible window after focus moves, so the carousel moves by whole cards and
 * never shows one cut at the edge. The window stays put while focus moves inside it.
 * @param windowStart first card visible now
 * @param focused card that just got focus
 * @param visible how many cards fit on screen
 * @returns first card to show
 */
fun windowStart(windowStart: Int, focused: Int, visible: Int = VISIBLE_CARDS): Int = when {
    focused < windowStart -> focused
    focused >= windowStart + visible -> focused - visible + 1
    else -> windowStart
}
