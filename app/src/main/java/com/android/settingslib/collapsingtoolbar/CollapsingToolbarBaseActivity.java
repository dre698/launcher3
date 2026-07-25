package com.android.settingslib.collapsingtoolbar;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Stub minimal buat CollapsingToolbarBaseActivity (aslinya dari SettingsLib
 * AOSP: frameworks/base/packages/SettingsLib, source-only, gak ada di
 * standalone project ini).
 *
 * Fungsinya cuma base activity biasa -- efek visual collapsing toolbar
 * (AppBarLayout + CollapsingToolbarLayout) hilang, tapi TrustAppsActivity
 * (fitur Hidden Apps Manager) tetap kompilasi & jalan normal.
 *
 * TODO: kalau nanti mau efek collapsing toolbar beneran, implementasikan
 * pake com.google.android.material.appbar.AppBarLayout +
 * CollapsingToolbarLayout manual di layout activity_hidden_apps.xml.
 */
public class CollapsingToolbarBaseActivity extends AppCompatActivity {
}
