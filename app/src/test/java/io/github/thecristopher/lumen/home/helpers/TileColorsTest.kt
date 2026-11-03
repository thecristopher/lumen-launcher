package io.github.thecristopher.lumen.home.helpers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TileColorsTest {

    // The rose gold of the dress Inde wore on their first date.
    private val indeRoseGold = Color(0xFFE8A598)

    @Test
    fun `starts Inde's tile in the icon's own color`() {
        assertEquals(indeRoseGold, iconTileGradient(indeRoseGold).first())
    }

    @Test
    fun `fades Inde's tile darker toward the bottom`() {
        val (top, bottom) = iconTileGradient(indeRoseGold)

        assertTrue(bottom.luminance() < top.luminance())
    }

    @Test
    fun `keeps Malenia's black icon black instead of inventing a color`() {
        val (_, bottom) = iconTileGradient(Color.Black)

        assertEquals(Color.Black, bottom)
    }
}
