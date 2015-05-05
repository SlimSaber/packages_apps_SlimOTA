package com.fusionjack.slimota.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.fusionjack.slimota.MainActivity;
import com.fusionjack.slimota.R;
import com.fusionjack.slimota.dialog.OTADialogHandler;
import com.fusionjack.slimota.parser.OTADevice;
import com.fusionjack.slimota.parser.OTAParser;
import com.fusionjack.slimota.utils.OTAUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fusionjack on 29.04.15.
 */
public class OTACheckerTask extends AsyncTask<Context, Void, OTADevice> {

    private static OTACheckerTask mAsyncTaskInstance = null;
    private final Handler mHandler = new OTADialogHandler();
    private Context mContext;
    private boolean mIsBackgroundThread;

    private OTACheckerTask(boolean isBackgroundThread) {
        this.mIsBackgroundThread = isBackgroundThread;
    }

    public static OTACheckerTask getInstance(boolean isBackgroundThread) {
        if (mAsyncTaskInstance == null) {
            mAsyncTaskInstance = new OTACheckerTask(isBackgroundThread);
        }
        return mAsyncTaskInstance;
    }

    @Override
    protected OTADevice doInBackground(Context... params) {
        mContext = params[0];

        if (!isConnectivityAvailable(mContext)) {
            return null;
        }

        showProgressDialog();

        OTADevice device = null;
        String deviceName = OTAUtils.getDeviceName(mContext);
        OTAUtils.logInfo("deviceName: " + deviceName);
        if (!deviceName.isEmpty()) {
            try {
                InputStream is = OTAUtils.downloadURL(mContext.getString(R.string.ota_url));
                if (is != null) {
                    final String releaseType = OTAUtils.getReleaseType(mContext);
                    device = OTAParser.getInstance().parse(is, deviceName, releaseType);
                    is.close();
                }
            } catch (IOException | XmlPullParserException e) {
                OTAUtils.logError(e);
            }
        }

        return device;
    }

    @Override
    protected void onPostExecute(OTADevice device) {
        super.onPostExecute(device);

        boolean updateAvailable = OTAUtils.checkVersion(device, mContext);
        if (updateAvailable) {
            OTASettings.persistLatestVersion(device, mContext);
            showNotification(mContext);
        }

        OTASettings.persistLastCheck(mContext);
        OTASettings.persistUrls(device, mContext);

        hideProgressDialog();

        mAsyncTaskInstance = null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mAsyncTaskInstance = null;
    }

    private void showProgressDialog() {
        if (!mIsBackgroundThread) {
            Message msg = mHandler.obtainMessage(OTADialogHandler.MSG_SHOW_DIALOG);
            msg.obj = mContext;
            mHandler.sendMessage(msg);
        }
    }

    private void hideProgressDialog() {
        if (!mIsBackgroundThread) {
            Message msg = mHandler.obtainMessage(OTADialogHandler.MSG_CLOSE_DIALOG);
            mHandler.sendMessage(msg);
        }
    }

    private static boolean isConnectivityAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    private void showNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(context.getString(R.string.notification_title));
        builder.setContentText(context.getString(R.string.notification_message));
        builder.setSmallIcon(R.drawable.ic_notification_slimota);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_slimota));

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1000001, notification);
    }
}
