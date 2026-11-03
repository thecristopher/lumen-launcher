package io.github.thecristopher.lumen.home.helpers

import org.junit.Assert.assertEquals
import org.junit.Test

class ScrollAnchorTest {

    @Test
    fun `keeps the screen still while Inde browses the Top Shelf`() {
        assertEquals(0, scrollAnchor(focusedItem = 0))
    }

    @Test
    fun `keeps the screen still on the dock so the shelf stays whole`() {
        assertEquals(0, scrollAnchor(focusedItem = DOCK_ITEM))
    }

    @Test
    fun `lifts Claire's recents row to the top once she moves below the dock`() {
        assertEquals(2, scrollAnchor(focusedItem = 2))
    }

    @Test
    fun `lifts a grid row deep in Malenia's apps to the top`() {
        assertEquals(5, scrollAnchor(focusedItem = 5))
    }
}
