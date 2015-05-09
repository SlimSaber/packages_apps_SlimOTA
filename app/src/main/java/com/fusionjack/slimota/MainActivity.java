package com.fusionjack.slimota;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.fusionjack.slimota.core.LinkConfig;
import com.fusionjack.slimota.dialog.OTADialogFragment;
import com.fusionjack.slimota.fragments.SlimOTAFragment;


public class MainActivity extends PreferenceActivity implements
        OTADialogFragment.OTADialogListener, LinkConfig.LinkConfigListener {

    private static final String FRAGMENT_TAG = SlimOTAFragment.class.getName();
    private SlimOTAFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragment = (SlimOTAFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (mFragment == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SlimOTAFragment(), FRAGMENT_TAG)
                    .commit();
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressCancelled() {
        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment instanceof OTADialogFragment.OTADialogListener) {
            ((OTADialogFragment.OTADialogListener) fragment).onProgressCancelled();
        }
    }

    @Override
    public void onConfigChange() {
        Fragment fragment = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment instanceof LinkConfig.LinkConfigListener) {
            ((LinkConfig.LinkConfigListener) fragment).onConfigChange();
        }
    }
}
