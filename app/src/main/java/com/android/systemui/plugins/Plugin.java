package com.android.systemui.plugins;

import android.content.Context;

/**
 * Interface dasar buat semua plugin SystemUI/Launcher. Di standalone build ini
 * plugin manager beneran (yang jalan di proses SystemUI) gak ada, jadi
 * interface ini murni buat kompilasi -- gak akan pernah benar-benar
 * di-trigger sama plugin manager (lihat PluginManagerWrapper yang semua
 * method-nya no-op).
 *
 * Aslinya dari PluginCoreLib (AOSP frameworks/base SystemUI plugin core),
 * ditulis ulang minimal di sini.
 */
public interface Plugin {
    default void onCreate(Context context) {}
    default void onDestroy() {}
}
