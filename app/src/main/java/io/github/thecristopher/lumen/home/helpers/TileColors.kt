package io.github.thecristopher.lumen.home.helpers

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/** How far toward black the bottom of an icon tile goes. */
private const val ICON_TILE_SHADE = 0.6f

/**
 * Gradient stops for a tile that only has an icon: the icon's own color on top, fading darker
 * toward the bottom so the icon sits on something that belongs to it.
 * @param glow the icon's dominant color
 * @returns top and bottom colors of the tile
 */
fun iconTileGradient(glow: Color): List<Color> =
    listOf(glow, lerp(glow, Color.Black, ICON_TILE_SHADE))
