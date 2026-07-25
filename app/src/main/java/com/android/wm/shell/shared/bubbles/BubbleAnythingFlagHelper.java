package com.android.wm.shell.shared.bubbles;

/**
 * Stub buat BubbleAnythingFlagHelper (fitur bubble chat, butuh proses
 * WindowManagerShell asli yang gak ada di standalone build). Return false
 * biar semua kode path bubble gak aktif.
 */
public final class BubbleAnythingFlagHelper {
    private BubbleAnythingFlagHelper() {}

    public static boolean enableCreateAnyBubble() {
        return false;
    }
}
