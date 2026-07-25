package com.android.launcher3;

/**
 * Stub buat aconfig flags library (com_android_launcher3_flags_lib) yang
 * aslinya di-generate otomatis dari file *.aconfig di folder aconfig/
 * (butuh Soong build system AOSP, gak bisa di-generate lewat Gradle biasa).
 *
 * Semua method sengaja return false (semua fitur experimental/baru OFF),
 * biar behaviour sedeket mungkin ke baseline stabil dan gak ada kode path
 * setengah-jadi yang ke-trigger.
 *
 * Kalau salah satu fitur ini mau diaktifin manual, tinggal ganti return
 * value method yang bersangkutan ke true.
 */
public final class Flags {
    private Flags() {}

    public static boolean allAppsBlur() { return false; }
    public static boolean allAppsSheetForHandheld() { return false; }
    public static boolean allowPrivateProfile() { return false; }
    public static boolean enableAllAppsButtonInHotseat() { return false; }
    public static boolean enableExpressiveFolderExpansion() { return false; }
    public static boolean enableFocusOutline() { return false; }
    public static boolean enableGridOnlyOverview() { return false; }
    public static boolean enableGsf() { return false; }
    public static boolean enableHomeTransitionListener() { return false; }
    public static boolean enableLauncherIconShapes() { return false; }
    public static boolean enableLauncherVisualRefresh() { return false; }
    public static boolean enableOverviewIconMenu() { return false; }
    public static boolean enableOverviewOnConnectedDisplays() { return false; }
    public static boolean enablePrivateSpace() { return false; }
    public static boolean enableQsbOnHotseat() { return false; }
    public static boolean enableRefactorTaskThumbnail() { return false; }
    public static boolean enableResponsiveWorkspace() { return false; }
    public static boolean enableRetrievableBubbles() { return false; }
    public static boolean enableScalabilityForDesktopExperience() { return false; }
    public static boolean enableSupportForArchiving() { return false; }
    public static boolean enableTaskbarPinning() { return false; }
    public static boolean enableTwoPaneLauncherSettings() { return false; }
    public static boolean enableWidgetPickerRefactor() { return false; }
    public static boolean extendibleThemeManager() { return false; }
    public static boolean floatingSearchBar() { return false; }
    public static boolean generatedPreviews() { return false; }
    public static boolean homeScreenEditImprovements() { return false; }
    public static boolean injectableModelItems() { return false; }
    public static boolean letterFastScroller() { return false; }
    public static boolean modelRepository() { return false; }
    public static boolean msdlFeedback() { return false; }
    public static boolean newCustomizationPickerUi() { return false; }
    public static boolean oneGridRotationHandling() { return false; }
    public static boolean oneGridSpecs() { return false; }
    public static boolean privateSpaceAddFloatingMaskView() { return false; }
    public static boolean privateSpaceAnimation() { return false; }
    public static boolean privateSpaceRestrictAccessibilityDrag() { return false; }
    public static boolean privateSpaceRestrictItemDrag() { return false; }
    public static boolean privateSpaceSysAppsSeparation() { return false; }
    public static boolean restoreArchivedAppIconsFromDb() { return false; }
    public static boolean restoreArchivedShortcuts() { return false; }
    public static boolean showFilesOnHomeScreen() { return false; }
    public static boolean showHomeBehindDesktop() { return false; }
    public static boolean simplifiedLauncherModelBinding() { return false; }
    public static boolean useNewIconForArchivedApps() { return false; }
    public static boolean workSchedulerInWorkProfile() { return false; }
}
