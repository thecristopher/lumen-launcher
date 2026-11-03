# Lumen

A calm, tvOS inspired home screen for Fire TV and Android TV. Big rounded tiles that tilt and catch the
light when focused, a backdrop that takes the color of the app you are on, and nothing trying to sell
you anything.

Lumen is inspired by tvOS, it does not copy it: no Apple names, icons, fonts or assets.

## Status

Early. The first version draws the home screen:

- every launchable app, with its TV banner when it has one
- a top row of five favorites and a grid under it
- focus that scales, lifts, sways in 3D and slides a shine across the tile
- a backdrop glowing in the focused app's color
- a clock
- a Top Shelf with what you were watching in the focused dock app, read from the system TV provider
  (its watch next list, or its preview channel), opening the exact episode on OK
- a Recents row under the dock with the last five apps you opened, for apps that publish nothing

Coming next: reordering, a control center and an aerial screensaver. The interactive mockup in [`mockup/lumen.html`](mockup/lumen.html) shows where it is going.

## Build

You need JDK 17 and the Android SDK (platform 35, build tools 34).

```sh
./gradlew test            # unit tests
./gradlew assembleDebug   # app/build/outputs/apk/debug/app-debug.apk
```

## Install on a Fire TV

1. Turn on ADB debugging on the Fire TV (Settings, My Fire TV, Developer options).
2. Install the APK:

   ```sh
   adb connect <fire-tv-ip>:5555
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

3. Lumen asks for permission to read TV listings on first launch, the Top Shelf needs it. Over ADB:

   ```sh
   adb shell pm grant io.github.thecristopher.lumen com.android.providers.tv.permission.READ_EPG_DATA
   adb shell pm grant io.github.thecristopher.lumen android.permission.READ_TV_LISTINGS
   ```

4. Fire OS ignores the default home setting, so the Home button needs a redirect. Install
   [Home on Fire](https://github.com/toolicious/home-on-fire) and pick Lumen as its target app.

On Android TV and Google TV devices Lumen can be picked as the home app directly.

## Project layout

```
app/src/main/java/io/github/thecristopher/lumen/
  MainActivity.kt          entry point, opens apps
  apps/                    installed apps, their artwork, default favorites
    helpers/               pure ordering logic, unit tested
  shelf/                   Top Shelf content from the TV provider
    helpers/               pure picking logic, unit tested
  home/HomeScreen.kt       the home screen, orchestration only
  home/actions/            loading steps for the home screen
  home/helpers/            scroll behavior, unit tested
  home/components/         tile, backdrop, clock, Top Shelf
  ui/theme/                colors and type
```

## License

MIT
