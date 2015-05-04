package com.fusionjack.slimota.core;

import android.content.Intent;
import android.os.AsyncTask;

import com.commonsware.cwac.wakeful.WakefulIntentService;

/**
 * Created by fusionjack on 29.04.15.
 */
public class OTAService extends WakefulIntentService {

    public OTAService() {
        super("SlimOTA");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        OTAChecker otaChecker = new OTAChecker(true);
        otaChecker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getApplicationContext());
    }
}
