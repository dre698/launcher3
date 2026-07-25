package com.android.systemui.plugins.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation buat versioning interface plugin. Di standalone build cuma
 * metadata, gak ada plugin manager beneran yang baca ini buat validasi
 * kompatibilitas versi.
 *
 * Aslinya dari PluginCoreLib (AOSP frameworks/base SystemUI plugin core),
 * ditulis ulang minimal di sini.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProvidesInterface {
    String action();
    int version();
    String parent() default "";
}
