package io.github.thecristopher.lumen.home.helpers

import org.junit.Assert.assertEquals
import org.junit.Test

class CardWindowTest {

    @Test
    fun `stays put while Inde moves between the cards already on screen`() {
        assertEquals(0, windowStart(windowStart = 0, focused = 3))
    }

    @Test
    fun `slides one card when Claire steps past the right edge`() {
        assertEquals(1, windowStart(windowStart = 0, focused = 4))
    }

    @Test
    fun `slides back when Malenia steps past the left edge`() {
        assertEquals(2, windowStart(windowStart = 3, focused = 2))
    }

    @Test
    fun `keeps the window when Inde comes back inside it`() {
        assertEquals(3, windowStart(windowStart = 3, focused = 5))
    }
}
