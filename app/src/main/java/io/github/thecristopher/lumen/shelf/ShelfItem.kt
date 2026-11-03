package io.github.thecristopher.lumen.shelf

/**
 * One piece of content an app published to the system TV provider.
 * @property packageName app that published it
 * @property title show or movie name
 * @property subtitle episode title, when the app gives one
 * @property posterUri artwork, usually an https or content uri
 * @property intentUri deep link that opens this content in its app
 * @property lastEngagement when it was last watched, 0 when unknown
 * @property channelName name of the preview channel it came from, null for the watch next list
 */
data class ShelfItem(
    val packageName: String,
    val title: String,
    val subtitle: String? = null,
    val posterUri: String? = null,
    val intentUri: String? = null,
    val lastEngagement: Long = 0L,
    val channelName: String? = null,
)

/**
 * What the Top Shelf shows for one app.
 * @property channelName name of the preview channel, null when the row is the watch next list
 * @property items the cards, in display order
 */
data class ShelfRow(val channelName: String?, val items: List<ShelfItem>)
