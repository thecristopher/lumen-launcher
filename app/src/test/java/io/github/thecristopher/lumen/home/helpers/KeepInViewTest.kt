package io.github.thecristopher.lumen.home.helpers

import org.junit.Assert.assertEquals
import org.junit.Test

class KeepInViewTest {

    // Claire's anniversary row: 130 tall in a 540 tall screen.
    private val rowSize = 130f
    private val screen = 540f

    @Test
    fun `leaves Claire's row alone when it is already on screen`() {
        assertEquals(0f, scrollToReveal(offset = 300f, size = rowSize, containerSize = screen))
    }

    @Test
    fun `scrolls down just enough to show Malenia's row below the fold`() {
        assertEquals(40f, scrollToReveal(offset = 450f, size = rowSize, containerSize = screen))
    }

    @Test
    fun `scrolls back up to show Inde's row above the top`() {
        assertEquals(-60f, scrollToReveal(offset = -60f, size = rowSize, containerSize = screen))
    }
}
