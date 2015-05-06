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
import com.fusionjack.slimota.core.OTACheckerTask;
import com.fusionjack.slimota.core.OTASettings;
import com.fusionjack.slimota.dialog.OTADialogFragment;
import com.fusionjack.slimota.parser.OTADevice;
import com.fusionjack.slimota.parser.OTAParser;
import com.fusionjack.slimota.utils.OTAUtils;

/**
 * Created by fusionjack on 30.04.15.
 */
public class SlimOTAFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener ,
        OTADialogFragment.OTADialogListener {

    private static final String KEY_ROM_INFO = "key_rom_info";
    private static final String KEY_CHECK_UPDATE = "key_check_update";
    private static final String KEY_UPDATE_INTERVAL = "key_update_interval";
    private static final String CATEGORY_LINKS = "category_links";

    private PreferenceScreen mRomInfo;
    private PreferenceScreen mCheckUpdate;
    private ListPreference mUpdateInterval;
    private PreferenceCategory mLinksCategory;

    private OTACheckerTask mTask;

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
        if (mLinksCategory != null) {
            OTADevice device = OTASettings.getUrls(getActivity());
            for (String key : device.getUrls()) {
                PreferenceScreen urlPref = (PreferenceScreen) getPreferenceScreen().findPreference(key);
                if (urlPref == null) {
                    urlPref = getPreferenceManager().createPreferenceScreen(getActivity());
                    urlPref.setKey(key);
                    mLinksCategory.addPreference(urlPref);
                }
            }
        }

        updatePreferences();
    }

    private void updatePreferences() {
        updateRomInfo();
        updateLastCheckSummary();
        updateIntervalSummary();
        updateUrls();
    }

    private void updateUrls() {
        OTADevice device = OTASettings.getUrls(getActivity());
        for (String key : device.getUrls()) {
            PreferenceScreen urlPref = (PreferenceScreen) getPreferenceScreen().findPreference(key);
            if (urlPref == null && mLinksCategory != null) {
                urlPref = getPreferenceManager().createPreferenceScreen(getActivity());
                urlPref.setKey(key);
                mLinksCategory.addPreference(urlPref);
            }
            if (urlPref != null) {
                urlPref.setTitle(OTAParser.stripUrlfromKey(key));
                String url = device.getUrl(key);
                urlPref.setSummary(url.isEmpty() ? "" : url);
            }
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
    public void onProgressCancelled() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        final String key = preference.getKey();
        switch (key) {
            case KEY_CHECK_UPDATE:
                mTask = OTACheckerTask.getInstance(false);
                if (!mTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getActivity());
                }
                return true;
            default:
                if (OTAParser.isUrlKey(key)) {
                    OTAUtils.launchUrl(OTASettings.getUrl(key, getActivity()), getActivity());
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
        if (OTAParser.isUrlKey(key)) {
            updateUrls();
        }
    }
}
