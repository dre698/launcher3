package com.android.launcher3.util.override;

import android.content.Context;
import android.util.Log;

/**
 * Marker interface buat class yang implementasinya bisa di-override lewat
 * string resource (misalnya device/product tertentu nyediain subclass custom
 * lewat overlay resource).
 *
 * Ditulis ulang di sini (aslinya com.android.launcher3.util.ResourceBasedOverride
 * di source AOSP standar) karena gak ikut ter-copy ke source tree ini.
 */
public interface ResourceBasedOverride {

    /**
     * Membuat instance dari kelas dasar {@code base}, atau instance dari
     * kelas custom yang namanya didefinisikan di string resource
     * {@code resId}, kalau resource itu diisi (gak kosong).
     */
    class Overrides {
        private static final String TAG = "ResourceBasedOverride";

        public static <T extends ResourceBasedOverride> T getObject(
                Class<T> base, Context context, int resId) {
            String className = context.getString(resId);
            if (className != null && !className.isEmpty()) {
                try {
                    Class<?> clazz = Class.forName(className);
                    return (T) clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    Log.e(TAG, "Gagal load override class: " + className
                            + ", fallback ke default: " + base.getName(), e);
                }
            }
            try {
                return base.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Gagal instantiate default class: " + base.getName(), e);
            }
        }

        private Overrides() {}
    }
}
