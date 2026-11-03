package io.github.thecristopher.lumen.apps.helpers

import io.github.thecristopher.lumen.apps.LaunchableApp
import org.junit.Assert.assertEquals
import org.junit.Test

class RecentsTest {

    private val claire = LaunchableApp("org.terrasave.claire", "Claire's Road Trip Mixtape")
    private val inde = LaunchableApp("love.graceful.inde", "Inde's Anniversary Cinema")
    private val malenia = LaunchableApp("co.haligtree.malenia", "Malenia's Love Letters")

    @Test
    fun `puts the app Inde just opened first`() {
        val history = recordLaunch(listOf(claire.packageName), inde.packageName)

        assertEquals(listOf(inde.packageName, claire.packageName), history)
    }

    @Test
    fun `moves Claire to the front instead of listing her twice`() {
        val history = recordLaunch(listOf(inde.packageName, claire.packageName), claire.packageName)

        assertEquals(listOf(claire.packageName, inde.packageName), history)
    }

    @Test
    fun `forgets the oldest app once the row is full`() {
        val full = (1..RECENTS_SIZE).map { "com.roundtable.love_note_$it" }

        val history = recordLaunch(full, malenia.packageName)

        assertEquals(RECENTS_SIZE, history.size)
        assertEquals(malenia.packageName, history.first())
        assertEquals(false, "com.roundtable.love_note_$RECENTS_SIZE" in history)
    }

    @Test
    fun `keeps the history order when drawing the row`() {
        val apps = recentApps(listOf(malenia.packageName, inde.packageName), visible = listOf(inde, claire, malenia))

        assertEquals(listOf(malenia, inde), apps)
    }

    @Test
    fun `skips an app that was uninstalled or hidden`() {
        val apps = recentApps(listOf("com.umbrella.wesker", claire.packageName), visible = listOf(claire))

        assertEquals(listOf(claire), apps)
    }
}
