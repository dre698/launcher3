package com.android.wm.shell.shared.bubbles.logging;

/**
 * Stub buat EntryPoint (enum entry point buat telemetry bubble chat).
 * Karena fitur bubble di-disable (lihat BubbleAnythingFlagHelper), enum ini
 * cuma dipake buat resolusi tipe compile time, gak pernah beneran nge-log.
 */
public enum EntryPoint {
    ALL_APPS_ICON_MENU,
    TASKBAR_ICON_MENU,
    HOTSEAT_ICON_MENU,
    LAUNCHER_ICON_MENU,
}
