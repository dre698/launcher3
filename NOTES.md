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

## 5. Update progress (setelah beberapa ronde build di GitHub Actions)

Masalah yang udah kefix:
- AGP 9.x built-in Kotlin conflict → `android.builtInKotlin=false` di gradle.properties
- Dagger+KSP2 crash (`DaggerSuperficialValidation$UnexpectedException`) → `ksp.useKSP2=false`
- Font resource nama invalid (`SlateForOnePlus-*.ttf`) → di-rename lowercase+underscore
- 10 resource `@android:color/system_*_dim_*` gak ada di public SDK → di-fallback ke
  warna non-dim yang paling deket
- Attr `disabledIconAlpha`/`loadingIconColor` (aslinya dari PluginCoreLib internal)
  → dideklarasi ulang manual di `attrs_plugin_core_stub.xml`
- Folder `dagger/` di root repo (qualifier `@ApplicationContext` dkk) ketinggalan
  gak ke-copy → udah ditambahin ke `java/com/android/launcher3/dagger/`
- Style `Theme.SubSettingsBase.Expressive` (buat `TrustAppsActivity`, fitur
  LineageOS-specific) gak ada → diarahin ke `HomeSettings.Theme` yang valid
- `com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity`
  (dari SettingsLib AOSP, gak ada di repo) → dibikin stub minimal extends
  `AppCompatActivity` (efek visual collapsing toolbar hilang, fitur tetap jalan)
- **3 class fundamental hilang total dari seluruh repo**: `SafeCloseable`
  (`com.android.launcher3.util`), `ResourceBasedOverride` dan
  `MainThreadInitializedObject` (`com.android.launcher3.util.override`) —
  ditulis ulang berdasarkan implementasi standar AOSP Launcher3. Ini
  kemungkinan bakal kepake lagi di error-error berikutnya karena banyak file
  lain juga import ketiganya.
- `androidx.appcompat` ketinggalan gak ditambahin ke dependency

## 7. Round berikutnya: modul `modules/concurrent` ketinggalan

Qualifier `@LightweightBackground` (dan 7 qualifier terkait: `Ui`,
`ThreadPoolContext`, `BackgroundContext`, `UiContext`, `Background`,
`ThreadPool`, `LightweightBackgroundContext`) plus `ExecutorsModule.kt`
ternyata dari modul terpisah `modules/concurrent/` (punya `Android.bp`
sendiri) yang beneran dipasang ke Dagger graph lewat `LauncherAppModule`
(bareng `LauncherExecutorsModule` yang udah ada). Udah di-copy utuh ke
`java/com/android/launcher3/concurrent/`.

Ini juga butuh dependency baru: **Guava** (`com.google.common.util.concurrent.ListeningExecutorService`)
— udah ditambahin ke `libs.versions.toml`.

**Pelajaran:** kalau ada error qualifier/annotation Dagger yang gak ketemu
lagi, kemungkinan besar itu dari modul kecil terpisah yang masih ketinggalan
(pola yang sama kayak `dagger/` dan `src_plugins/` sebelumnya). Cek folder
root repo asli (`find . -maxdepth 1 -type d`) buat modul yang belum ke-copy.

## 8. Workflow: notifikasi Telegram sekarang kirim file log, bukan link

Step "Build APK" sekarang nge-tee output ke `build_log.txt`. Kalau build
gagal (APK gak ketemu), "Notify Telegram" ngirim 500 baris terakhir log itu
sebagai file lampiran (`sendDocument`), bukan link ke halaman Actions.

## 6. Kategori A & B: sudah di-stub proaktif

Gak nunggu error round berikutnya, saya udah nyiapin duluan:

- **Kategori A** (`com.android.systemui.plugins.*`): ternyata sebagian besar
  file-nya ASLI ada di source (`src_plugins/` folder di root repo, punya
  `LauncherPluginLib`) tapi ketinggalan gak ke-copy pas scaffold awal. Udah
  di-copy: `AllAppsRow`, `ResourceProvider`, `CustomWidgetPlugin`,
  `LauncherOverlayPlugin`, `LauncherOverlayManager` (+ shared), `OneSearch`,
  `HotseatPlugin`, `FirstScreenWidget`, `IconProcessorPlugin`,
  `NetworkFetcherPlugin`. Cuma `Plugin`, `PluginListener`, dan
  `annotations.ProvidesInterface` yang beneran gak ada di repo manapun
  (dari PluginCoreLib eksternal) — itu ditulis manual sebagai stub minimal.

- **Kategori B** (`wm.shell.Flags` dkk) + flags launcher3/systemui.shared:
  semua di-stub return `false`. Termasuk `com.android.launcher3.Flags` (44
  method, dari file aconfig di folder `aconfig/`), `com.android.systemui.shared.Flags`,
  `com.android.wm.shell.Flags`, `BlurUtils`, `SysUiStatsLog` (cuma konstanta
  int), `BubbleAnythingFlagHelper`, `EntryPoint` (enum).

**Kalau nanti error compile nyebut salah satu Flags method yang GAK ada di
list saya** (kemungkinan ada yang kelewatan), tinggal tambahin method baru
return `false` ke file Flags yang sesuai — polanya udah jelas kelihatan.

**Pola umum yang kelihatan sejauh ini:** source Launcher3-16.2 ini sepertinya
hasil reorganisasi/refactor branch Android 16 yang beberapa modul kecilnya
(qualifier Dagger, util.override, plugin lib, aconfig flags) ketinggalan pas
di-zip/di-share — bukan masalah dari cara kita nge-porting. Kalau masih ada
class serupa yang hilang di ronde build berikutnya, pola fix-nya sama: cek
apakah class itu genuinely gak ada di repo sama sekali
(`find . -iname "*NamaClass*"`), kalau iya tulis ulang minimal
implementation-nya berdasarkan API yang dipanggil sama file yang import dia.


