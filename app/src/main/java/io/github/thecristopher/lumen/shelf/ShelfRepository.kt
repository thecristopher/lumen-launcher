package io.github.thecristopher.lumen.shelf

import android.content.ContentResolver
import android.database.Cursor
import android.media.tv.TvContract
import android.net.Uri

/**
 * Reads what apps publish to the system TV provider: the watch next list and preview channels.
 * Needs READ_EPG_DATA and READ_TV_LISTINGS, without them every list comes back empty.
 */
class ShelfRepository(private val resolver: ContentResolver) {

    /**
     * Every watch next entry on the device, in no particular order.
     * @returns entries, empty when the provider refuses access
     */
    fun watchNext(): List<ShelfItem> =
        query(TvContract.WatchNextPrograms.CONTENT_URI) { cursor ->
            ShelfItem(
                packageName = cursor.text(COLUMN_PACKAGE) ?: return@query null,
                title = cursor.text(COLUMN_TITLE) ?: return@query null,
                subtitle = cursor.text(COLUMN_EPISODE),
                posterUri = cursor.text(COLUMN_POSTER),
                intentUri = cursor.text(COLUMN_INTENT),
                lastEngagement = cursor.number(COLUMN_LAST_ENGAGEMENT),
            )
        }

    /**
     * Every item in every browsable preview channel, grouped by channel in publish order.
     * @returns items, empty when the provider refuses access
     */
    fun previews(): List<ShelfItem> {
        val channels = previewChannels()
        val position = channels.withIndex().associate { (index, channel) -> channel.first to index }
        return query(TvContract.PreviewPrograms.CONTENT_URI) { cursor ->
            val channelId = cursor.number(COLUMN_CHANNEL_ID)
            val index = position[channelId] ?: return@query null
            val item = ShelfItem(
                packageName = cursor.text(COLUMN_PACKAGE) ?: return@query null,
                title = cursor.text(COLUMN_TITLE) ?: return@query null,
                subtitle = cursor.text(COLUMN_EPISODE),
                posterUri = cursor.text(COLUMN_POSTER),
                intentUri = cursor.text(COLUMN_INTENT),
                channelName = channels[index].second,
            )
            index to item
        }.sortedBy { it.first }.map { it.second }
    }

    /** Browsable preview channels as id and name pairs, in the provider's order. */
    private fun previewChannels(): List<Pair<Long, String>> =
        query(TvContract.Channels.CONTENT_URI) { cursor ->
            val isPreview = cursor.text(COLUMN_CHANNEL_TYPE) == TvContract.Channels.TYPE_PREVIEW
            val browsable = cursor.number(COLUMN_BROWSABLE) == 1L
            if (isPreview && browsable) cursor.number(COLUMN_ID) to cursor.text(COLUMN_DISPLAY_NAME).orEmpty() else null
        }

    // The provider throws SecurityException without the permissions; an empty shelf is the right answer then.
    private fun <T : Any> query(uri: Uri, read: (Cursor) -> T?): List<T> = runCatching {
        resolver.query(uri, null, null, null, null)?.use { cursor ->
            buildList { while (cursor.moveToNext()) read(cursor)?.let(::add) }
        }.orEmpty()
    }.getOrDefault(emptyList())

    private fun Cursor.text(column: String): String? =
        getColumnIndex(column).takeIf { it >= 0 }?.let { if (isNull(it)) null else getString(it) }

    private fun Cursor.number(column: String): Long =
        getColumnIndex(column).takeIf { it >= 0 }?.let { getLong(it) } ?: 0L

    private companion object {
        const val COLUMN_ID = "_id"
        const val COLUMN_PACKAGE = "package_name"
        const val COLUMN_TITLE = "title"
        const val COLUMN_EPISODE = "episode_title"
        const val COLUMN_POSTER = "poster_art_uri"
        const val COLUMN_INTENT = "intent_uri"
        const val COLUMN_LAST_ENGAGEMENT = "last_engagement_time_utc_millis"
        const val COLUMN_CHANNEL_ID = "channel_id"
        const val COLUMN_CHANNEL_TYPE = "type"
        const val COLUMN_BROWSABLE = "browsable"
        const val COLUMN_DISPLAY_NAME = "display_name"
    }
}
