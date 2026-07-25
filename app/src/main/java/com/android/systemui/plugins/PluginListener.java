package com.android.systemui.plugins;

import android.content.Context;

/**
 * Listener buat notifikasi connect/disconnect plugin. Di standalone build ini
 * gak pernah benar-benar dipanggil (plugin manager beneran gak ada), tapi
 * banyak class (CustomWidgetManager, FloatingHeaderView, dll) implements
 * interface ini jadi harus tetap ada biar kompilasi.
 *
 * Aslinya dari PluginCoreLib (AOSP frameworks/base SystemUI plugin core),
 * ditulis ulang minimal di sini.
 */
public interface PluginListener<T extends Plugin> {
    default void onPluginConnected(T plugin, Context context) {}
    default void onPluginDisconnected(T plugin) {}
}
