package io.github.thecristopher.lumen.home.helpers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec

/**
 * Scroll needed to show an item, or zero when it is already fully visible. Compose on TV pins the
 * focused item at a pivot by default, which would push the Top Shelf off screen on the first focus.
 * @param offset item start relative to the container, negative when it starts above it
 * @param size item length along the scroll axis
 * @param containerSize visible length of the container
 * @returns how far to scroll, negative to go back
 */
fun scrollToReveal(offset: Float, size: Float, containerSize: Float): Float {
    val end = offset + size
    return when {
        offset >= 0f && end <= containerSize -> 0f
        offset < 0f -> offset
        else -> end - containerSize
    }
}

/** Scrolls only as far as needed to reveal the focused item. */
@OptIn(ExperimentalFoundationApi::class)
val KeepInViewSpec = object : BringIntoViewSpec {
    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float =
        scrollToReveal(offset, size, containerSize)
}
