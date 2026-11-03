package io.github.thecristopher.lumen.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import io.github.thecristopher.lumen.ui.theme.LumenType
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val HOURS_MINUTES = DateTimeFormatter.ofPattern("HH:mm")
private const val TICK_MS = 1_000L

/** The time in the top right corner, 24 hour format. */
@Composable
fun Clock(modifier: Modifier = Modifier) {
    val time by produceState(initialValue = now()) {
        while (true) {
            delay(TICK_MS)
            value = now()
        }
    }
    Text(time, style = LumenType.Clock, modifier = modifier)
}

private fun now(): String = LocalTime.now().format(HOURS_MINUTES)
