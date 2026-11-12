PROJECT: HiddenCamDetector, an Android-only app that estimates hidden-camera RISK in a room using only phone sensors (Wi-Fi, BLE, LAN, camera/torch).

HARD PRODUCT RULES
- Never say "all cameras detected" or "room is safe". Output is a risk level: LOW / MEDIUM / HIGH.
- Every report lists "Checked" and "Not checked / cannot be detected by phone" (cellular cameras, offline SD-card cameras without IR LEDs, wired cameras on other segments).
- All scanning is on-device. No network upload of scan data. INTERNET permission is only for the LAN scan.
- Show a disclaimer on Home and Report screens.

STACK: Kotlin, Jetpack Compose (Material3), MVVM (ViewModel + StateFlow), Coroutines/Flow, Navigation Compose, Room, DataStore, CameraX + Camera2 interop, OpenCV Android SDK. minSdk 26, targetSdk latest. No Hilt, no TFLite, no Firebase.

PACKAGE: com.yourapp.hiddencam with subpackages ui, scan, fusion, data, util, viewmodel.

CODE RULES
- Every file must compile. No TODOs, no placeholder bodies, no pseudo-code.
- Scanners are interfaces with Android implementations, so they can be faked in tests.
- UI state is a single immutable data class exposed as StateFlow per screen.
- Heavy work on Dispatchers.Default/IO; never block the main thread.
- Always handle: permission denied, Bluetooth off, Location off, Wi-Fi off, no camera/torch, scan throttled.
- Prefer small files (<200 lines). Use dependency versions via a libs.versions.toml catalog.
- After generating code, list the files created/changed and how to verify them. Do not explain basic Kotlin.
