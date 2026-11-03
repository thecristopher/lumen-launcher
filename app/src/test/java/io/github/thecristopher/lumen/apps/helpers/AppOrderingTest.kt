package io.github.thecristopher.lumen.apps.helpers

import io.github.thecristopher.lumen.apps.LaunchableApp
import org.junit.Assert.assertEquals
import org.junit.Test

class AppOrderingTest {

    private val claire = LaunchableApp("org.terrasave.claire", "Claire's Road Trip Mixtape")
    private val malenia = LaunchableApp("co.haligtree.malenia", "malenia's love letters")
    private val inde = LaunchableApp("love.graceful.inde", "Inde's Anniversary Cinema")
    private val leon = LaunchableApp("gov.dso.leon", "Leon's Date Night Picks")
    private val wesker = LaunchableApp("com.umbrella.wesker", "Wesker Surveillance")
    private val melina = LaunchableApp("co.roundtable.melina", "Melina's Shared Playlist")

    @Test
    fun `pins Inde and Claire first in the order they were preferred`() {
        val ordered = orderApps(
            installed = listOf(claire, malenia, inde),
            preferred = listOf(inde.packageName, claire.packageName),
            hidden = emptySet(),
        )

        assertEquals(listOf(inde, claire, malenia), ordered)
    }

    @Test
    fun `sorts the apps nobody pinned by label ignoring case`() {
        val ordered = orderApps(
            installed = listOf(leon, malenia, claire),
            preferred = emptyList(),
            hidden = emptySet(),
        )

        assertEquals(listOf(claire, leon, malenia), ordered)
    }

    @Test
    fun `keeps Wesker hidden even when someone pins him`() {
        val ordered = orderApps(
            installed = listOf(wesker, inde),
            preferred = listOf(wesker.packageName, inde.packageName),
            hidden = setOf(wesker.packageName),
        )

        assertEquals(listOf(inde), ordered)
    }

    @Test
    fun `skips a preferred app that is not installed`() {
        val ordered = orderApps(
            installed = listOf(malenia),
            preferred = listOf(inde.packageName, malenia.packageName),
            hidden = emptySet(),
        )

        assertEquals(listOf(malenia), ordered)
    }

    @Test
    fun `pins Malenia once when she is preferred twice`() {
        val ordered = orderApps(
            installed = listOf(malenia, claire),
            preferred = listOf(malenia.packageName, malenia.packageName),
            hidden = emptySet(),
        )

        assertEquals(listOf(malenia, claire), ordered)
    }

    @Test
    fun `puts five apps in the dock and the rest in the grid`() {
        val apps = listOf(inde, claire, malenia, leon, melina, wesker)

        val layout = splitHome(apps)

        assertEquals(listOf(inde, claire, malenia, leon, melina), layout.dock)
        assertEquals(listOf(wesker), layout.grid)
    }

    @Test
    fun `keeps a short list entirely in the dock`() {
        val layout = splitHome(listOf(claire, inde))

        assertEquals(listOf(claire, inde), layout.dock)
        assertEquals(emptyList<LaunchableApp>(), layout.grid)
    }
}
