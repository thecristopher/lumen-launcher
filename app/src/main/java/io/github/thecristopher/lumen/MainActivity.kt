package io.github.thecristopher.lumen

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import io.github.thecristopher.lumen.apps.AppRepository
import io.github.thecristopher.lumen.apps.LaunchableApp
import io.github.thecristopher.lumen.home.HomeScreen
import io.github.thecristopher.lumen.shelf.ShelfItem
import io.github.thecristopher.lumen.shelf.ShelfRepository
import io.github.thecristopher.lumen.ui.theme.LumenTheme

/** Entry point and home activity: draws the home screen and opens apps and shelf content from it. */
class MainActivity : ComponentActivity() {

    private val repository by lazy { AppRepository(packageManager) }
    private val shelfRepository by lazy { ShelfRepository(contentResolver) }

    // Bumped on every Home press so the screen goes back to the first app, like tvOS.
    private var homePresses by mutableIntStateOf(0)

    // Bumped every time Lumen comes back, so the Top Shelf shows what you just watched.
    private var refreshes by mutableIntStateOf(0)

    private val askTvPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { refreshes++ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForMissingTvPermissions()
        setContent {
            LumenTheme {
                HomeScreen(
                    repository = repository,
                    shelfRepository = shelfRepository,
                    homePresses = homePresses,
                    refreshes = refreshes,
                    onLaunch = ::launch,
                    onOpen = ::open,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        homePresses++
    }

    override fun onResume() {
        super.onResume()
        refreshes++
    }

    private fun askForMissingTvPermissions() {
        val missing = TV_PERMISSIONS.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) askTvPermissions.launch(missing.toTypedArray())
    }

    private fun launch(app: LaunchableApp) {
        repository.launchIntent(app)?.let(::startActivity)
    }

    // Opens the exact episode when the app gave a deep link, the app itself otherwise.
    private fun open(item: ShelfItem) {
        val deepLink = item.intentUri?.let { runCatching { Intent.parseUri(it, Intent.URI_INTENT_SCHEME) }.getOrNull() }
        val opened = deepLink != null && runCatching {
            startActivity(deepLink.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }.isSuccess
        if (!opened) launch(LaunchableApp(item.packageName, item.title))
    }

    private companion object {
        val TV_PERMISSIONS = listOf(
            "com.android.providers.tv.permission.READ_EPG_DATA",
            "android.permission.READ_TV_LISTINGS",
        )
    }
}
