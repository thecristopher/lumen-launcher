package io.github.thecristopher.lumen.shelf.helpers

import io.github.thecristopher.lumen.shelf.ShelfItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShelfPickingTest {

    private val cinema = "love.graceful.inde.cinema"
    private val haligtree = "co.haligtree.malenia.tv"

    private fun watched(title: String, at: Long, app: String = cinema) =
        ShelfItem(packageName = app, title = title, lastEngagement = at)

    private fun preview(title: String, channel: String, app: String = cinema) =
        ShelfItem(packageName = app, title = title, channelName = channel)

    @Test
    fun `puts the movie Inde and Claire watched last night first`() {
        val row = pickShelf(
            cinema,
            watchNext = listOf(watched("Anniversary Rewatch", at = 100), watched("Last Night's Date Movie", at = 900)),
            previews = emptyList(),
        )

        assertEquals(listOf("Last Night's Date Movie", "Anniversary Rewatch"), row?.items?.map { it.title })
        assertNull(row?.channelName)
    }

    @Test
    fun `ignores what Malenia is watching on another app`() {
        val row = pickShelf(
            cinema,
            watchNext = listOf(watched("Scarlet Rot Documentary", at = 500, app = haligtree), watched("Love Letters", at = 1)),
            previews = emptyList(),
        )

        assertEquals(listOf("Love Letters"), row?.items?.map { it.title })
    }

    @Test
    fun `shows a show once even when two episodes are in progress`() {
        val row = pickShelf(
            cinema,
            watchNext = listOf(watched("Shared Playlist Stories", at = 2), watched("Shared Playlist Stories", at = 1)),
            previews = emptyList(),
        )

        assertEquals(1, row?.items?.size)
    }

    @Test
    fun `falls back to the first preview channel when nothing is in progress`() {
        val row = pickShelf(
            cinema,
            watchNext = emptyList(),
            previews = listOf(
                preview("Date Night Picks", channel = "For Claire"),
                preview("Rainy Day Romance", channel = "For Claire"),
                preview("Boss Fights", channel = "Trending"),
            ),
        )

        assertEquals("For Claire", row?.channelName)
        assertEquals(listOf("Date Night Picks", "Rainy Day Romance"), row?.items?.map { it.title })
    }

    @Test
    fun `stops at the shelf limit`() {
        val many = (1..15).map { watched("Love Note $it", at = it.toLong()) }

        val row = pickShelf(cinema, watchNext = many, previews = emptyList())

        assertEquals(SHELF_MAX_ITEMS, row?.items?.size)
    }

    @Test
    fun `returns nothing when the app published nothing`() {
        assertNull(pickShelf(cinema, watchNext = emptyList(), previews = emptyList()))
    }
}
