package com.android.wm.shell;

/**
 * Stub buat aconfig flags library (com_android_wm_shell_flags_lib), sama
 * kayak com.android.launcher3.Flags -- ditulis manual, semua return false
 * (fitur bubble dkk yang butuh proses WindowManagerShell asli emang gak
 * akan jalan di standalone build ini).
 */
public final class Flags {
    private Flags() {}

    public static boolean enableGsf() { return false; }
}
