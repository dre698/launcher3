package com.android.launcher3.util.override;

import android.content.Context;

/**
 * Utility buat lazy singleton yang diinisialisasi sekali dan di-cache.
 * Versi "override" ini gak punya batasan generic {@code T extends SafeCloseable}
 * (beda dari versi non-override), jadi bisa dipakai buat tipe apa pun
 * (misalnya Room's {@code RoomDatabase} di {@code AppDatabase.kt}).
 *
 * Ditulis ulang di sini (aslinya com.android.launcher3.util.MainThreadInitializedObject
 * di source AOSP standar) karena gak ikut ter-copy ke source tree ini.
 */
public class MainThreadInitializedObject<T> {

    private final ObjectProvider<T> mProvider;
    private volatile T mValue;

    public MainThreadInitializedObject(ObjectProvider<T> provider) {
        mProvider = provider;
    }

    public T get(Context context) {
        if (mValue == null) {
            synchronized (this) {
                if (mValue == null) {
                    mValue = mProvider.get(context.getApplicationContext());
                }
            }
        }
        return mValue;
    }

    /**
     * Bikin {@link MainThreadInitializedObject} yang instance-nya di-resolve
     * lewat {@link ResourceBasedOverride.Overrides#getObject}: pakai class
     * custom dari string resource {@code resId} kalau ada, atau fallback ke
     * {@code base}.
     */
    public static <T extends ResourceBasedOverride> MainThreadInitializedObject<T> forOverride(
            Class<T> base, int resId) {
        return new MainThreadInitializedObject<>(
                context -> ResourceBasedOverride.Overrides.getObject(base, context, resId));
    }

    public interface ObjectProvider<T> {
        T get(Context context);
    }
}
