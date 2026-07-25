package com.android.launcher3.util;

/**
 * Sama seperti {@link AutoCloseable}, tapi tanpa checked exception di method
 * close(). Class ini aslinya ada di com.android.launcher3.util di source AOSP
 * standar, ditulis ulang di sini karena gak ikut ter-copy ke source tree
 * Launcher3-16.2 yang dipakai project ini.
 */
public interface SafeCloseable extends AutoCloseable {
    @Override
    void close();
}
