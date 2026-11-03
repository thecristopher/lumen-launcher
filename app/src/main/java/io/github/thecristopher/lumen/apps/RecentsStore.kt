package io.github.thecristopher.lumen.apps

import android.content.Context
import io.github.thecristopher.lumen.apps.helpers.recordLaunch

/** Keeps the launch history across restarts, most recent first. */
class RecentsStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /**
     * Reads the saved history.
     * @returns package names, most recent first, empty on first run
     */
    fun read(): List<String> =
        prefs.getString(KEY, null)?.split(SEPARATOR)?.filter { it.isNotBlank() }.orEmpty()

    /**
     * Records that an app was opened and saves the new history.
     * @param packageName the app just opened
     * @returns the updated history, most recent first
     */
    fun record(packageName: String): List<String> {
        val history = recordLaunch(read(), packageName)
        prefs.edit().putString(KEY, history.joinToString(SEPARATOR)).apply()
        return history
    }

    private companion object {
        const val PREFS = "lumen"
        const val KEY = "recent_packages"
        const val SEPARATOR = ","
    }
}
