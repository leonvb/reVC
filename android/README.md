# Android builds

The Android project is under `launcher/`. To build locally, install JDK 17 and
Android SDK components `platforms;android-35`, `ndk;27.2.12479018`, and
`cmake;3.22.1`, then run:

```sh
cd android/launcher
./gradlew assembleDebug assembleRelease
```

Both APKs are written under `launcher/app/build/outputs/apk/`.

## GitHub Actions

The `reVC Android` workflow is manual-only. Open **Actions → reVC Android → Run
workflow**, choose the branch, and run it. The run uploads separate `revc-debug`
and `revc-release` artifacts, each containing an installable APK. The release
variant uses the Android debug signing key so it can be sideloaded; it is not a
Play Store release signing configuration.

The APK builds the engine and packages its Android native dependencies, but it
does not contain the proprietary Vice City game data. On first launch, grant the
requested file-access permission, then copy the contents of a legally obtained
game-data installation into the `/reVC/` folder at the root of shared storage.
The app can then be reopened to start the game.
