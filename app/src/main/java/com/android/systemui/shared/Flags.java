package com.android.systemui.shared;

/**
 * Stub buat aconfig flags library (com_android_systemui_shared_flags_lib),
 * sama kayak com.android.launcher3.Flags -- gak bisa di-generate lewat
 * Gradle biasa, jadi ditulis manual dengan semua method return false.
 */
public final class Flags {
    private Flags() {}

    public static boolean newCustomizationPickerUi() { return false; }
    public static boolean extendibleThemeManager() { return false; }
}
