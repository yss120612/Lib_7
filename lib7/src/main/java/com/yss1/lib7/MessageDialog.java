package com.yss1.lib7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MessageDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private String eText;
    private String eTitle;
    private View form = null;
    private TextView mViewText;
    private JmeFragmentBase jmeFragment;

    public MessageDialog() {
        // Empty constructor required for DialogFragment
    }

    public MessageDialog init(String eTitle, String eText, JmeFragmentBase jmeFragment) {
        this.eText = eText;
        this.eTitle = eTitle;
        this.jmeFragment=jmeFragment;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewText = (TextView) form.findViewById(jmeFragment.getID(101));
        mViewText.setText(eText);
        getDialog().setTitle(eTitle);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        form = getActivity().getLayoutInflater().inflate(jmeFragment.getID(1), null);
        adb.setView(form).
                setPositiveButton("OK", this).
                setTitle("Ti");
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }
}
