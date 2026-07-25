package com.android.systemui.shared.system;

/**
 * Stub buat BlurUtils (aslinya dari SystemUI-statsd/systemui shared lib,
 * butuh akses SurfaceFlinger privileged yang gak ada di standalone app).
 * Konsisten sama temuan sebelumnya soal ANGLE/background blur non-fungsional
 * di device tanpa privileged access.
 */
public final class BlurUtils {
    private BlurUtils() {}

    public static boolean supportsBlursOnWindows() {
        return false;
    }
}
