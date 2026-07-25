package com.android.systemui.shared.system;

/**
 * Stub buat SysUiStatsLog (aslinya class hasil generate dari statsd atom
 * definitions, dipake buat telemetry logging internal Google). Di standalone
 * build ini cuma dipake buat nentuin kode integer kategori user type -- aman
 * di-stub, gak ada logging beneran yang jalan (gak ada proses statsd).
 */
public final class SysUiStatsLog {
    private SysUiStatsLog() {}

    public static final int LAUNCHER_UICHANGED__USER_TYPE__TYPE_UNKNOWN = 0;
    public static final int LAUNCHER_UICHANGED__USER_TYPE__TYPE_MAIN = 1;
    public static final int LAUNCHER_UICHANGED__USER_TYPE__TYPE_WORK = 2;
    public static final int LAUNCHER_UICHANGED__USER_TYPE__TYPE_CLONED = 3;
    public static final int LAUNCHER_UICHANGED__USER_TYPE__TYPE_PRIVATE = 4;
}
