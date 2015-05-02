package com.fusionjack.slimota.core;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.fusionjack.slimota.R;
import com.fusionjack.slimota.parser.OTADevice;
import com.fusionjack.slimota.utils.OTAUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fusionjack on 01.05.15.
 */
public final class OTASettings {

    private static final String LAST_CHECK = "last_check";
    private static final String UPDATE_INTERVAL = "update_interval";
    private static final String LATEST_VERSION = "latest_version";
    private static final String ROM_URL = "rom_url";
    private static final String GAPPS_URL = "gapps_url";
    private static final String CHANGELOG_URL = "changelog_url";

    private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("yy/MM/dd HH:mm:ss", Locale.US);

    private OTASettings() {
    }

    public static String getLatestRomNameKey() {
        return LATEST_VERSION;
    }

    public static String getLastCheckKey() {
        return LAST_CHECK;
    }

    public static String getUpdateIntervalKey() {
        return UPDATE_INTERVAL;
    }

    public static String getRomUrl() {
        return ROM_URL;
    }

    public static String getGappsUrl() {
        return GAPPS_URL;
    }

    public static String getChangelogUrl() {
        return CHANGELOG_URL;
    }

    public static boolean isSystemUpToDate(Context context) {
        final String currentVersion = OTAUtils.getCurrentVersion(context);
        final String latestVersion = getLatestVersion(context);
        return !OTAUtils.checkVersion(currentVersion, latestVersion, context);
    }

    public static String buildLastCheckSummary(long time, Context context) {
        String prefix = context.getResources().getString(R.string.last_check_summary);
        if (time > 0) {
            final String date = DATE_FORMATER.format(new Date(time));
            return String.format(prefix, date);
        }
        return String.format(prefix, context.getResources().getString(R.string.last_check_never));
    }

    public static String getLastCheck(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final long time = sharedPreferences.getLong(LAST_CHECK, 0);
        return buildLastCheckSummary(time, context);
    }

    public static String getLatestVersion(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(LATEST_VERSION, "");
    }

    public static OTADevice getDevice(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String filename = sharedPreferences.getString(LATEST_VERSION, "");
        String romUrl = sharedPreferences.getString(ROM_URL, "");
        String gappsUrl = sharedPreferences.getString(GAPPS_URL, "");
        String changelogUrl = sharedPreferences.getString(CHANGELOG_URL, "");
        return new OTADevice(filename, romUrl, changelogUrl, gappsUrl);
    }

    public static void persistUrls(OTADevice device, Context context) {
        if (device != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String url = sharedPreferences.getString(ROM_URL, "");
            if (url.isEmpty()) {
                sharedPreferences.edit().putString(ROM_URL, device.getDownloadUrl()).apply();
                sharedPreferences.edit().putString(GAPPS_URL, device.getGappsUrl()).apply();
                sharedPreferences.edit().putString(CHANGELOG_URL, device.getChangelogUrl()).apply();
            }
        }
    }

    public static void persistLatestVersion(OTADevice device, Context context) {
        if (device != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPreferences.edit().putString(LATEST_VERSION, device.getFilename()).apply();
        }
    }

    public static void persistLastCheck(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(LAST_CHECK, System.currentTimeMillis()).apply();
    }

    public static void persistUpdateIntervalIndex(int intervalIndex, Context context) {
        long intervalValue;
        switch(intervalIndex) {
            case 0:
                intervalValue = 0;
                break;
            case 1:
                intervalValue = AlarmManager.INTERVAL_HOUR;
                break;
            case 2:
                intervalValue = AlarmManager.INTERVAL_HALF_DAY;
                break;
            case 3:
                intervalValue = AlarmManager.INTERVAL_DAY;
                break;
            default:
                intervalValue = OTAListener.DEFAULT_INTERVAL_VALUE;
                break;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(UPDATE_INTERVAL, intervalValue).apply();
        if (intervalValue > 0) {
            WakefulIntentService.cancelAlarms(context);
            WakefulIntentService.scheduleAlarms(new OTAListener(), context, true);
            Toast.makeText(context, context.getResources().getString(R.string.autoupdate_enabled),
                    Toast.LENGTH_LONG).show();
        } else {
            WakefulIntentService.cancelAlarms(context);
            Toast.makeText(context, context.getResources().getString(R.string.autoupdate_disabled),
                    Toast.LENGTH_LONG).show();
        }
    }

    public static int getUpdateIntervalIndex(Context context) {
        long value = getUpdateIntervalTime(context);
        int index;
        if (value == 0) {
            index = 0;
        } else if (value == AlarmManager.INTERVAL_HOUR) {
            index = 1;
        } else if (value == AlarmManager.INTERVAL_HALF_DAY) {
            index = 2;
        } else {
            index = 3;
        }
        return index;
    }

    public static long getUpdateIntervalTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(UPDATE_INTERVAL, OTAListener.DEFAULT_INTERVAL_VALUE);
    }
}
