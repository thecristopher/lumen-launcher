package io.github.thecristopher.lumen.apps

/**
 * An app the home screen can show and open.
 * @property packageName unique id used for ordering and launching
 * @property label name shown under the focused tile
 */
data class LaunchableApp(val packageName: String, val label: String)

/**
 * The home screen split the tvOS way: a top row and the grid under it.
 * @property dock apps in the top row, the ones that drive the Top Shelf
 * @property grid every other visible app, in order
 */
data class HomeLayout(val dock: List<LaunchableApp>, val grid: List<LaunchableApp>) {
    companion object {
        val EMPTY = HomeLayout(emptyList(), emptyList())
    }
}
