# NOTES.md — Porting Launcher3 (Android 16.2) ke Gradle standalone

Status: scaffold awal. Project INI BELUM PERNAH DI-COMPILE (sandbox saya gak ada
akses internet buat download Gradle/dependency), jadi anggap ini starting point,
bukan hasil final. Kamu compile & fix error iteratif di Termux/Android Studio.

## 1. Yang udah beres
- Struktur Gradle app module (`app/build.gradle.kts`, `settings.gradle.kts`)
- Source `src/` + `src_no_quickstep/` + `src_build_config/` digabung ke
  `app/src/main/java`
- `res/` dan `AndroidManifest-common.xml` udah dicopy
- Dependency AndroidX/Maven standar (recyclerview, room, dagger, coroutines, dll)
  udah dipetakan di `build.gradle.kts`

## 2. Kategori masalah & cara nanganinnya

### A. `com.android.systemui.plugins.*` (Plugin, PluginListener, ResourceProvider,
   AllAppsRow, CustomWidgetPlugin, LauncherOverlayManager, dll)
Ini interface plugin architecture SystemUI — dipakai di `Launcher.java`,
`Workspace.java`, `DragLayer.java`, `FloatingHeaderView.java`, `PluginHeaderRow.java`,
`ActivityAllAppsContainerView.java`, `CustomWidgetManager.java`,
`PluginManagerWrapper.java`, `OverlayEdgeEffect.java`, `DynamicResource.java`,
`WorkspaceStateTransitionAnimation.java`, `FlingSpringAnim.java`.

**Cara paling gampang:** interface-interface ini kecil (kebanyakan cuma method
signature). Bikin ulang sebagai interface Kotlin/Java lokal di
`app/src/main/java/com/android/systemui/plugins/` dengan isi minimal (no-op),
karena tanpa proses SystemUI asli, plugin system ini emang gak akan pernah
ke-trigger — cukup bikin kompilasi lolos.

### B. `com.android.wm.shell.*` (Flags, bubbles)
Muncul di `WidgetsModel.java`, `WorkspaceItemInfo.java`, `DeviceProfile.java`,
`DeepShortcutTextView.java`, `PopupContainerWithArrow.kt`, `PopupDataSource.kt`,
`SystemShortcut.java`.

**Cara nanganin:** ini flag aconfig buat fitur bubble (chat bubble kayak Messenger).
Bikin object stub `com.android.wm.shell.Flags` yang semua method-nya return
`false` — otomatis semua kode path fitur bubble gak akan aktif (aman, karena
bubble butuh proses WindowManagerShell asli yang gak ada di standalone build).

### C. `com.android.internal.util.lunaris.*` (khusus ROM Lunaris)
Muncul di `QuickspaceController.java` (widget cuaca/quickspace ala OmniJaws),
`LauncherIconProvider.java`, `OptionsPopupView.java`.

**Ini bukan API AOSP** — spesifik ROM Lunaris yang kamu pake sekarang. Gak akan
ada di standalone build sama sekali. **Rekomendasi: hapus/disable fitur ini**
(quickspace weather bar) daripada coba reimplementasi OmniJawsClient dari nol.

### D. `android.app.IActivityManager` + `ActivityManagerNative` (genuinely hidden API)
Di `SystemShortcut.java` baris ~641. Ini hidden API asli Android framework,
gak ada workaround gampang lewat public SDK biasa. Perlu reflection
(`Class.forName("android.app.ActivityManagerNative")...`) atau pake lib
`hiddenapibypass` seperti yang dipakai Lawnchair/Rootless Pixel Launcher. Kalau
fitur yang manggil ini gak esensial, lebih aman di-stub return null / no-op.

### E. `com.android.systemui.shared.*` (SysUiStatsLog, BlurUtils, Flags)
Muncul di `ItemInfo.java`, `SettingsMisc.java`, `PreviewSurfaceRenderer.java`,
`ShapesProvider.kt`, `NotificationBadgeCounter.java`, `PreviewContext.kt`,
`PreviewLifecycleObserver.kt`, `GridCustomizationsProxy.java`.
- `SysUiStatsLog` → stub jadi no-op (cuma logging telemetry, aman dihapus)
- `BlurUtils` → stub return false/no blur (background blur butuh SurfaceFlinger
  API yang gak jalan tanpa privileged access — sesuai temuan kamu sebelumnya soal
  ANGLE/blur di MT6762 juga non-fungsional)
- `Flags` (shared) → sama kayak poin B, stub return false semua

### F. Dependency library, bukan cuma API
- `frameworks/libs/systemui:iconloader_base, view_capture, animationlib,
  contextualeducationlib, mechanics, msdl` — source-only, gak ada di Maven.
  Ambil source-nya dari AOSP `frameworks/libs/systemui` (bisa clone shallow
  cuma folder itu), taruh sebagai module lokal `:vendor:iconloader` dst.
- `WindowManager-Shell-shared-AOSP` — sama, source-only.
- `org.lineageos.platform` — SDK LineageOS. Kalau standalone (bukan device
  Lineage), hapus pemakaiannya atau stub.
- `chaldea.seraphixgoogle` — source-nya ADA di repo ini sendiri
  (folder `seraphixgoogle/`), belum dipindah ke module ini. Include manual
  kalau mau pertahankan fitur Google Feed card.
- `libGoogleFeed` (libs/libGoogleFeed.jar) — closed-source prebuilt. Copy ke
  `app/libs/` kalau tetap mau dipakai, atau skip fiturnya (lebih simpel).

## 3. Urutan kerja yang disaranin
1. Compile dulu, biarin error numpuk, baca daftar class-not-found
2. Kelompokkan error sesuai kategori A-F di atas
3. Stub dulu semua yang bisa di-stub (A, B, E) — ini bakal ngilangin
   80% error tanpa kerja berat
4. Hapus fitur Lunaris-specific (C) — quickspace weather bar
5. Terakhir baru handle hidden API asli (D) dan vendor library (F) yang emang
   butuh effort lebih

## 4. Yang PASTI ilang/gak jalan di standalone (gak worth dikejar)
- Deep integration recents/overview animation (folder `quickstep/` full di-skip,
  sudah benar sejak awal)
- Bubble chat feature (butuh proses WindowManagerShell)
- Background blur (butuh privileged SurfaceFlinger access)
- Quickspace weather bar ala Lunaris ROM
- Auto-replace default Home tanpa prompt user (privileged-only)
