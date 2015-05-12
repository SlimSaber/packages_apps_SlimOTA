/*
 * Copyright (C) 2015 Chandra Poerwanto
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

package com.fusionjack.slimota.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.fusionjack.slimota.R;
import com.fusionjack.slimota.configs.AppConfig;
import com.fusionjack.slimota.configs.LinkConfig;
import com.fusionjack.slimota.configs.OTAVersion;
import com.fusionjack.slimota.dialogs.WaitDialogFragment;
import com.fusionjack.slimota.tasks.CheckUpdateTask;
import com.fusionjack.slimota.utils.OTAUtils;
import com.fusionjack.slimota.xml.OTALink;

import java.util.List;

public class SlimOTAFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener ,
        WaitDialogFragment.OTADialogListener,
        LinkConfig.LinkConfigListener {

    private static final String KEY_ROM_INFO = "key_rom_info";
    private static final String KEY_CHECK_UPDATE = "key_check_update";
    private static final String KEY_UPDATE_INTERVAL = "key_update_interval";
    private static final String CATEGORY_LINKS = "category_links";

    private PreferenceScreen mRomInfo;
    private PreferenceScreen mCheckUpdate;
    private ListPreference mUpdateInterval;
    private PreferenceCategory mLinksCategory;

    private CheckUpdateTask mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        addPreferencesFromResource(R.xml.slimota);

        mRomInfo = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_ROM_INFO);
        mCheckUpdate = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_CHECK_UPDATE);

        mUpdateInterval = (ListPreference) getPreferenceScreen().findPreference(KEY_UPDATE_INTERVAL);
        if (mUpdateInterval != null) {
            mUpdateInterval.setOnPreferenceChangeListener(this);
        }

        mLinksCategory = (PreferenceCategory) getPreferenceScreen().findPreference(CATEGORY_LINKS);
    }

    private void updatePreferences() {
        updateRomInfo();
        updateLastCheckSummary();
        updateIntervalSummary();
        updateLinks(false);
    }

    private void updateLinks(boolean force) {
        List<OTALink> links = LinkConfig.getInstance().getLinks(getActivity(), force);
        for (OTALink link : links) {
            String id = link.getId();
            PreferenceScreen linkPref = (PreferenceScreen) getPreferenceScreen().findPreference(id);
            if (linkPref == null && mLinksCategory != null) {
                linkPref = getPreferenceManager().createPreferenceScreen(getActivity());
                linkPref.setKey(id);
                mLinksCategory.addPreference(linkPref);
            }
            if (linkPref != null) {
                String title = link.getTitle();
                linkPref.setTitle(title.isEmpty() ? id : title);
                linkPref.setSummary(link.getDescription());
            }
        }
    }

    private void updateRomInfo() {
        if (mRomInfo != null) {
            String fullLocalVersion = OTAVersion.getFullLocalVersion(getActivity());
            String shortLocalVersion = OTAVersion.extractVersionFrom(fullLocalVersion, getActivity());
            mRomInfo.setTitle(fullLocalVersion);

            String prefix = getActivity().getResources().getString(R.string.latest_version);
            String fullLatestVersion = AppConfig.getFullLatestVersion(getActivity());
            String shortLatestVersion = OTAVersion.extractVersionFrom(fullLatestVersion, getActivity());
            if (fullLatestVersion.isEmpty()) {
                fullLatestVersion = getActivity().getResources().getString(R.string.unknown);
                mRomInfo.setSummary(String.format(prefix, fullLatestVersion));
            } else if (!OTAVersion.compareVersion(shortLatestVersion, shortLocalVersion, getActivity())) {
                mRomInfo.setSummary(getActivity().getResources().getString(R.string.system_uptodate));
            } else {
                mRomInfo.setSummary(String.format(prefix, fullLatestVersion));
            }
        }
    }

    private void updateLastCheckSummary() {
        if (mCheckUpdate != null) {
            mCheckUpdate.setSummary(AppConfig.getLastCheck(getActivity()));
        }
    }

    private void updateIntervalSummary() {
        if (mUpdateInterval != null) {
            mUpdateInterval.setValueIndex(AppConfig.getUpdateIntervalIndex(getActivity()));
            mUpdateInterval.setSummary(mUpdateInterval.getEntry());
        }
    }

    @Override
     public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updatePreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onProgressCancelled() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    @Override
    public void onConfigChange() {
        updateLinks(true);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final String key = preference.getKey();
        switch (key) {
            case KEY_CHECK_UPDATE:
                mTask = CheckUpdateTask.getInstance(false);
                if (!mTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity());
                }
                return true;
            default:
                OTALink link = LinkConfig.getInstance().findLink(key, getActivity());
                if (link != null) {
                    OTAUtils.launchUrl(link.getUrl(), getActivity());
                }
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mUpdateInterval) {
            AppConfig.persistUpdateIntervalIndex(Integer.valueOf((String) value), getActivity());
            return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(AppConfig.getLatestVersionKey())) {
            updateRomInfo();
        }
        if (key.equals(AppConfig.getLastCheckKey())) {
            updateLastCheckSummary();
        }
        if (key.equals(AppConfig.getUpdateIntervalKey())) {
            updateIntervalSummary();
        }
    }
}
