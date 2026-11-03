package io.github.thecristopher.lumen.apps

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette

/**
 * Artwork for one tile.
 * @property image the banner, or the icon when the app has no banner
 * @property wide true for a 16:9 banner that fills the tile, false for an icon to center
 * @property glow color the backdrop takes while the tile has focus
 */
data class AppArt(val image: ImageBitmap, val wide: Boolean, val glow: Color)

/** Reads launchable apps, their artwork and their launch intents from the package manager. */
class AppRepository(private val packageManager: PackageManager) {

    /**
     * Lists apps that open from a TV or a phone launcher, one entry per package.
     * @returns apps in no particular order, see orderApps for the home order
     */
    fun launchableApps(): List<LaunchableApp> =
        (query(Intent.CATEGORY_LEANBACK_LAUNCHER) + query(Intent.CATEGORY_LAUNCHER))
            .distinctBy { it.activityInfo.packageName }
            .map { LaunchableApp(it.activityInfo.packageName, it.loadLabel(packageManager).toString()) }

    /**
     * Loads the tile artwork, preferring the TV banner over the icon. Slow, call it off the main thread.
     * @param app the app to draw
     * @returns the artwork, or null when the app has neither banner nor icon
     */
    fun art(app: LaunchableApp): AppArt? {
        val banner = bannerOf(app.packageName)
        val drawable = banner ?: iconOf(app.packageName) ?: return null
        val bitmap = drawable.toBitmap(
            width = drawable.intrinsicWidth.takeIf { it > 0 } ?: BANNER_WIDTH,
            height = drawable.intrinsicHeight.takeIf { it > 0 } ?: BANNER_HEIGHT,
        )
        val palette = Palette.from(bitmap).generate()
        val glow = Color(palette.getVibrantColor(palette.getDominantColor(FALLBACK_GLOW)))
        return AppArt(bitmap.asImageBitmap(), wide = banner != null, glow = glow)
    }

    /**
     * Builds the intent that opens the app, preferring its TV entry point.
     * @param app the app to open
     * @returns a ready to start intent, or null when the app cannot be opened
     */
    fun launchIntent(app: LaunchableApp): Intent? =
        (packageManager.getLeanbackLaunchIntentForPackage(app.packageName)
            ?: packageManager.getLaunchIntentForPackage(app.packageName))
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    private fun query(category: String): List<ResolveInfo> =
        packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(category), 0)

    private fun bannerOf(packageName: String): Drawable? {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
            .setPackage(packageName)
        val activity = packageManager.queryIntentActivities(intent, 0).firstOrNull()?.activityInfo
        return activity?.loadBanner(packageManager)
            ?: runCatching { packageManager.getApplicationBanner(packageName) }.getOrNull()
    }

    // Asks for the sharpest icon the app ships, the default density looks blurry once scaled to a tile.
    private fun iconOf(packageName: String): Drawable? =
        runCatching {
            val info = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getResourcesForApplication(info)
                .getDrawableForDensity(info.icon, DisplayMetrics.DENSITY_XXXHIGH, null)
        }.getOrNull() ?: runCatching { packageManager.getApplicationIcon(packageName) }.getOrNull()

    private companion object {
        const val BANNER_WIDTH = 320
        const val BANNER_HEIGHT = 180
        const val FALLBACK_GLOW = 0xFF3A3D48.toInt()
    }
}
