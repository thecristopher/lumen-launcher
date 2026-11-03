package io.github.thecristopher.lumen.apps.helpers

import io.github.thecristopher.lumen.apps.LaunchableApp

/** How many apps the Recents row remembers and shows. */
const val RECENTS_SIZE = 5

/**
 * Moves an app to the front of the launch history.
 * @param history package names, most recent first
 * @param packageName the app just opened
 * @param size how many to keep
 * @returns the new history, most recent first, without duplicates
 */
fun recordLaunch(history: List<String>, packageName: String, size: Int = RECENTS_SIZE): List<String> =
    (listOf(packageName) + history.filterNot { it == packageName }).take(size)

/**
 * Turns the launch history into apps the home screen can draw.
 * @param history package names, most recent first
 * @param visible apps currently on the home screen, hidden and uninstalled ones are left out
 * @returns the recent apps, most recent first
 */
fun recentApps(history: List<String>, visible: List<LaunchableApp>): List<LaunchableApp> {
    val byPackage = visible.associateBy { it.packageName }
    return history.mapNotNull { byPackage[it] }
}
