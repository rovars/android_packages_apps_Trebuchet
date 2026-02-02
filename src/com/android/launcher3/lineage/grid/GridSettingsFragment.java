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
package com.android.launcher3.lineage.grid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;

import android.content.Intent;

import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherFiles;
import com.android.launcher3.R;

public final class GridSettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final PreferenceManager prefManager = getPreferenceManager();
        prefManager.setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
        final Context context = prefManager.getContext();

        final SharedPreferences prefs = prefManager.getSharedPreferences();
        sanitizeIntPreference(prefs, InvariantDeviceProfile.KEY_DRAWER_COLUMNS);
        sanitizeIntPreference(prefs, InvariantDeviceProfile.KEY_WORKSPACE_COLUMNS);
        sanitizeIntPreference(prefs, InvariantDeviceProfile.KEY_DOCK_COLUMNS);
        sanitizeStringPreference(prefs, InvariantDeviceProfile.KEY_ICON_SIZE);
        sanitizeStringPreference(prefs, InvariantDeviceProfile.KEY_ALL_APPS_ICON_HEIGHT);

        setPreferencesFromResource(R.xml.launcher_grid_preferences, rootKey);

        final InvariantDeviceProfile idp = InvariantDeviceProfile.INSTANCE.get(context);

        // Update default values for list preferences to match current IDP
        ListPreference drawerCols = findPreference(InvariantDeviceProfile.KEY_DRAWER_COLUMNS);
        if (drawerCols != null) {
            if (drawerCols.getValue() == null) {
                drawerCols.setValue(String.valueOf(idp.numAllAppsColumns));
            }
            drawerCols.setOnPreferenceChangeListener(this);
        }

        ListPreference workspaceCols = findPreference(InvariantDeviceProfile.KEY_WORKSPACE_COLUMNS);
        if (workspaceCols != null) {
            if (workspaceCols.getValue() == null) {
                workspaceCols.setValue(String.valueOf(idp.numColumns));
            }
            workspaceCols.setOnPreferenceChangeListener(this);
        }

        ListPreference dockCols = findPreference(InvariantDeviceProfile.KEY_DOCK_COLUMNS);
        if (dockCols != null) {
            if (dockCols.getValue() == null) {
                dockCols.setValue(String.valueOf(idp.numHotseatIcons));
            }
            dockCols.setOnPreferenceChangeListener(this);
        }

        SeekBarPreference iconSize = findPreference(InvariantDeviceProfile.KEY_ICON_SIZE);
        if (iconSize != null) {
            iconSize.setShowSeekBarValue(true);
            iconSize.setOnPreferenceChangeListener(this);
        }

        SeekBarPreference allAppsIconHeight = findPreference(InvariantDeviceProfile.KEY_ALL_APPS_ICON_HEIGHT);
        if (allAppsIconHeight != null) {
            allAppsIconHeight.setShowSeekBarValue(true);
            allAppsIconHeight.setOnPreferenceChangeListener(this);
        }

        Preference showLabels = findPreference(InvariantDeviceProfile.KEY_SHOW_DRAWER_LABELS);
        if (showLabels != null) {
            showLabels.setOnPreferenceChangeListener(this);
        }

        Preference landscapeLabels = findPreference(InvariantDeviceProfile.KEY_SHOW_LABELS_LANDSCAPE);
        if (landscapeLabels != null) {
            landscapeLabels.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // Trigger real-time update in InvariantDeviceProfile
        InvariantDeviceProfile.INSTANCE.get(getContext()).verifyConfigChangedInBackground(getContext());
        return true;
    }

    private void sanitizeIntPreference(SharedPreferences prefs, String key) {
        Object val = prefs.getAll().get(key);
        if (val instanceof Integer) {
            prefs.edit().putString(key, String.valueOf(val)).apply();
        }
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
