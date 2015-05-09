package com.fusionjack.slimota.scheduler;

import android.content.Intent;
import android.os.AsyncTask;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.fusionjack.slimota.tasks.CheckUpdateTask;

/**
 * Created by fusionjack on 29.04.15.
 */
public class OTAService extends WakefulIntentService {

    public OTAService() {
        super("SlimOTA");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        CheckUpdateTask otaChecker = CheckUpdateTask.getInstance(true);
        if (!otaChecker.getStatus().equals(AsyncTask.Status.RUNNING)) {
            otaChecker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getApplicationContext());
        }
    }
}
