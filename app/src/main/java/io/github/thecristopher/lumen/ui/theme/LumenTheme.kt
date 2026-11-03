package io.github.thecristopher.lumen.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

/** Colors shared by every Lumen screen. */
object LumenColors {
    val Ink = Color(0xFF07080B)
    val Text = Color(0xFFF4F5F8)
    val TextMuted = Color(0xBFF4F5F8)
    val TextFaint = Color(0x99F4F5F8)
    val Tile = Color(0xFF1C1E25)
}

/** Type scale for a 960x540dp TV canvas, read from three meters away. */
object LumenType {
    val Clock = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium, color = LumenColors.Text)
    val Eyebrow = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.8.sp, color = LumenColors.TextFaint)
    val Hero = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp, color = LumenColors.Text)
    val HeroLine = TextStyle(fontSize = 15.sp, color = LumenColors.TextMuted)
    val TileName = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = LumenColors.Text)
    val TileFallback = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = LumenColors.Text)
    val ShelfLabel = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LumenColors.TextMuted)
    val CardTitle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LumenColors.Text)
    val CardSubtitle = TextStyle(fontSize = 10.sp, color = LumenColors.TextMuted)
}

/**
 * Wraps the app in the TV Material theme with Lumen's dark palette.
 * @param content the screen to draw
 */
@Composable
fun LumenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(background = LumenColors.Ink, onBackground = LumenColors.Text),
        content = content,
    )
}
