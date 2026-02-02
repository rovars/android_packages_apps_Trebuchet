/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3.lineage.recents;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;

public final class RecentsSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);

        final SharedPreferences prefs = prefManager.getSharedPreferences();
        sanitizeStringPreference(prefs, "pref_overview_scrim_opacity");

        setPreferencesFromResource(R.xml.launcher_recents_preferences, rootKey);
    }

    private void sanitizeStringPreference(SharedPreferences prefs, String key) {
        Object val = prefs.getAll().get(key);
        if (val instanceof String) {
            try {
                prefs.edit().putInt(key, Integer.parseInt((String) val)).apply();
            } catch (NumberFormatException ignored) { }
        }
    }
}
