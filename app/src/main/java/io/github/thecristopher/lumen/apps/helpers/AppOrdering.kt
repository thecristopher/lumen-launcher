package io.github.thecristopher.lumen.apps.helpers

import io.github.thecristopher.lumen.apps.HomeLayout
import io.github.thecristopher.lumen.apps.LaunchableApp

/** How many apps the top row holds, like the tvOS dock. */
const val DOCK_SIZE = 5

/**
 * Sorts apps for the home screen: preferred ones first in the given order, then the rest by label.
 * @param installed every app that can be launched
 * @param preferred package names to pin first, the ones not installed are skipped
 * @param hidden package names that never show, even when preferred
 * @returns the visible apps in home order
 */
fun orderApps(
    installed: List<LaunchableApp>,
    preferred: List<String>,
    hidden: Set<String>,
): List<LaunchableApp> {
    val visible = installed.filterNot { it.packageName in hidden }
    val byPackage = visible.associateBy { it.packageName }
    val pinned = preferred.distinct().mapNotNull { byPackage[it] }
    val rest = visible.filterNot { it in pinned }.sortedBy { it.label.lowercase() }
    return pinned + rest
}

/**
 * Splits ordered apps into the top row and the grid below it.
 * @param apps visible apps already in home order
 * @param dockSize how many go in the top row
 * @returns the layout the home screen draws
 */
fun splitHome(apps: List<LaunchableApp>, dockSize: Int = DOCK_SIZE): HomeLayout =
    HomeLayout(dock = apps.take(dockSize), grid = apps.drop(dockSize))
