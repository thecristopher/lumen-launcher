package io.github.thecristopher.lumen.home.helpers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec

/**
 * Never scrolls on focus. Compose on TV pins the focused item at a pivot by default, which cuts rows
 * in half; Lumen's lists scroll themselves instead (see scrollAnchor and windowStart).
 */
@OptIn(ExperimentalFoundationApi::class)
val ManualScrollSpec = object : BringIntoViewSpec {
    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float = 0f
}
