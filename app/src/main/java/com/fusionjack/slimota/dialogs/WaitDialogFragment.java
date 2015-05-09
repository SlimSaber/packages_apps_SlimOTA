package com.fusionjack.slimota.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.fusionjack.slimota.R;

/**
 * Created by fusionjack on 05.05.15.
 */
public class WaitDialogFragment extends DialogFragment {

    public static WaitDialogFragment newInstance() {
        return new WaitDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(true);

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(getActivity().getString(R.string.dialog_message));
        return dialog;
    }

    @Override
    public void onDestroyView() {
        // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (getOTADialogListener() != null) {
            getOTADialogListener().onProgressCancelled();
        }
    }

    public interface OTADialogListener {
        void onProgressCancelled();
    }

    private OTADialogListener getOTADialogListener() {
        if (getActivity() instanceof OTADialogListener) {
            return (OTADialogListener) getActivity();
        }
        return null;
    }
}
