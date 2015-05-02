package com.fusionjack.slimota.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.fusionjack.slimota.utils.OTAUtils;

/**
 * Created by fusionjack on 29.04.15.
 */
public class OTAListener implements WakefulIntentService.AlarmListener {

    public static final long DEFAULT_INTERVAL_VALUE = AlarmManager.INTERVAL_HALF_DAY;

    private long mIntervalValue = DEFAULT_INTERVAL_VALUE;

    @Override
    public void scheduleAlarms(AlarmManager alarmManager, PendingIntent pendingIntent, Context context) {
        mIntervalValue = OTASettings.getUpdateIntervalTime(context);
        if (mIntervalValue > 0) {
            OTAUtils.logInfo("SlimOTA is scheduled for every: " + mIntervalValue + " ms");
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 60000, mIntervalValue, pendingIntent);
        } else {
            OTAUtils.logInfo("SlimOTA is disabled");
        }
    }

    @Override
    public void sendWakefulWork(Context context) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMan != null) {
            NetworkInfo netInfo = connMan.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                Intent backgroundIntent = new Intent(context, OTAService.class);
                WakefulIntentService.sendWakefulWork(context, backgroundIntent);
            }
        }
    }

    @Override
    public long getMaxAge() {
        return (mIntervalValue * 2);
    }
}
