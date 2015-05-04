package com.fusionjack.slimota.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.fusionjack.slimota.R;
import com.fusionjack.slimota.core.OTAChecker;
import com.fusionjack.slimota.core.OTASettings;
import com.fusionjack.slimota.parser.OTADevice;
import com.fusionjack.slimota.utils.OTAUtils;

/**
 * Created by fusionjack on 30.04.15.
 */
public class SlimOTAFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_ROM_INFO = "key_rom_info";
    private static final String KEY_CHECK_UPDATE = "key_check_update";
    private static final String KEY_UPDATE_INTERVAL = "key_update_interval";

    private static final String KEY_DOWNLOAD_ROM = "key_download_rom";
    private static final String KEY_DOWNLOAD_GAPPS = "key_download_gapps";
    private static final String KEY_DOWNLOAD_CHANGELOG = "key_download_changelog";

    private PreferenceScreen mRomInfo;
    private PreferenceScreen mCheckUpdate;
    private ListPreference mUpdateInterval;

    private PreferenceScreen mRomUrl;
    private PreferenceScreen mGappsUrl;
    private PreferenceScreen mChangelogUrl;

    private OTADevice mDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.slimota);

        mRomInfo = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_ROM_INFO);
        mCheckUpdate = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_CHECK_UPDATE);

        mUpdateInterval = (ListPreference) getPreferenceScreen().findPreference(KEY_UPDATE_INTERVAL);
        if (mUpdateInterval != null) {
            mUpdateInterval.setOnPreferenceChangeListener(this);
        }

        mRomUrl = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_DOWNLOAD_ROM);
        mGappsUrl = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_DOWNLOAD_GAPPS);
        mChangelogUrl = (PreferenceScreen) getPreferenceScreen().findPreference(KEY_DOWNLOAD_CHANGELOG);
    }

    private void updatePreferences() {
        updateRomInfo();
        updateLastCheckSummary();
        updateIntervalSummary();
        updateUrls();
    }

    private void updateUrls() {
        mDevice = OTASettings.getDevice(getActivity());
        if (mRomUrl != null) {
            String url = mDevice.getDownloadUrl();
            mRomUrl.setSummary(url.isEmpty() ? "" : url);
        }
        if (mGappsUrl != null) {
            String url = mDevice.getGappsUrl();
            mGappsUrl.setSummary(url.isEmpty() ? "" : url);
        }
        if (mChangelogUrl != null) {
            String url = mDevice.getChangelogUrl();
            mChangelogUrl.setSummary(url.isEmpty() ? "" : url);
        }
    }

    private void updateRomInfo() {
        if (mRomInfo != null) {
            final String currentVersion = OTAUtils.getCurrentVersion(getActivity());
            mRomInfo.setTitle(currentVersion);

            if (OTASettings.isSystemUpToDate(getActivity())) {
                mRomInfo.setSummary(getActivity().getResources().getString(R.string.system_uptodate));
            } else {
                String latestVersion = OTASettings.getLatestVersion(getActivity());
                if (latestVersion.isEmpty()) {
                    latestVersion = getActivity().getResources().getString(R.string.unknown);
                }
                String prefix = getActivity().getResources().getString(R.string.latest_version);
                mRomInfo.setSummary(String.format(prefix, latestVersion));
            }
        }
    }

    private void updateLastCheckSummary() {
        if (mCheckUpdate != null) {
            mCheckUpdate.setSummary(OTASettings.getLastCheck(getActivity()));
        }
    }

    private void updateIntervalSummary() {
        if (mUpdateInterval != null) {
            mUpdateInterval.setValueIndex(OTASettings.getUpdateIntervalIndex(getActivity()));
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
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final String key = preference.getKey();
        switch (key) {
            case KEY_CHECK_UPDATE:
                OTAChecker otaChecker = new OTAChecker(false);
                otaChecker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity());
                return true;
            case KEY_DOWNLOAD_ROM:
                if (mDevice != null) {
                    OTAUtils.launchUrl(mDevice.getDownloadUrl(), getActivity());
                }
                break;
            case KEY_DOWNLOAD_GAPPS:
                if (mDevice != null) {
                    OTAUtils.launchUrl(mDevice.getGappsUrl(), getActivity());
                }
                break;
            case KEY_DOWNLOAD_CHANGELOG:
                if (mDevice != null) {
                    OTAUtils.launchUrl(mDevice.getChangelogUrl(), getActivity());
                }
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mUpdateInterval) {
            OTASettings.persistUpdateIntervalIndex(Integer.valueOf((String) value), getActivity());
            return true;
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(OTASettings.getLatestRomNameKey())) {
            updateRomInfo();
        }
        if (key.equals(OTASettings.getLastCheckKey())) {
            updateLastCheckSummary();
        }
        if (key.equals(OTASettings.getUpdateIntervalKey())) {
            updateIntervalSummary();
        }
        if (key.equals(OTASettings.getRomUrl()) || key.equals(OTASettings.getGappsUrl()) ||
                key.equals(OTASettings.getChangelogUrl())) {
            updateUrls();
        }
    }
}
