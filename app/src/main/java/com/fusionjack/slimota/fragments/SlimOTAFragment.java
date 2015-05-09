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
import com.fusionjack.slimota.configs.LinkConfig;
import com.fusionjack.slimota.configs.OTAConfig;
import com.fusionjack.slimota.tasks.CheckUpdateTask;
import com.fusionjack.slimota.configs.AppConfig;
import com.fusionjack.slimota.dialogs.WaitDialogFragment;
import com.fusionjack.slimota.xml.OTALink;
import com.fusionjack.slimota.utils.OTAUtils;

import java.util.List;

/**
 * Created by fusionjack on 30.04.15.
 */
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

        updatePreferences();
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
            final String currentVersion = OTAConfig.getInstance(getActivity()).getCurrentVersion();
            mRomInfo.setTitle(currentVersion);

            String prefix = getActivity().getResources().getString(R.string.latest_version);
            String latestVersion = AppConfig.getLatestVersion(getActivity());
            if (latestVersion.isEmpty()) {
                latestVersion = getActivity().getResources().getString(R.string.unknown);
                mRomInfo.setSummary(String.format(prefix, latestVersion));
            } else if (!OTAUtils.compareVersion(currentVersion, latestVersion, getActivity())) {
                mRomInfo.setSummary(getActivity().getResources().getString(R.string.system_uptodate));
            } else {
                mRomInfo.setSummary(String.format(prefix, latestVersion));
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
        if (key.equals(AppConfig.getLatestRomNameKey())) {
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
