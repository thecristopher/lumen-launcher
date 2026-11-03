package io.github.thecristopher.lumen.apps

/** Streaming apps that start in the top row when installed, in this order. */
val DEFAULT_FAVORITES = listOf(
    "com.crunchyroll.crunchyroid",
    "com.netflix.ninja",
    "com.disney.disneyplus",
    "com.hbo.hbonow",
    "com.apple.atve.amazon.appletv",
    "com.amazon.firetv.youtube",
    "tv.twitch.android.viewer",
    "com.tubitv.ott",
)

/** Fire OS system entries and other launchers that would only crowd the home screen. */
val DEFAULT_HIDDEN = setOf(
    "io.github.thecristopher.lumen",
    "com.amazon.tv.launcher",
    "com.amazon.whasettings",
    "com.amazon.ftv.screensaver",
    "com.amazon.ftv.profilepicker",
    "com.spocky.projengmenu",
    "io.github.toolicious.homeonfire",
)
