package com.yss1.lib7;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.yss1.lib_jm.ToolsBase;

public class EditNameDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText mEditText;
    private String eText;
    private String eTitle;
    private int type;
    private View form = null;
    private JmeFragmentBase jmeFragment;

    public EditNameDialog() {
        // Empty constructor required for DialogFragment
    }

    public EditNameDialog init(int dt, String eTitle, String eText, JmeFragmentBase jmeFragment) {
        this.eText = eText;
        this.eTitle = eTitle;
        this.type = dt;
        this.jmeFragment = jmeFragment;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        mEditText = (EditText) form.findViewById(jmeFragment.getID(202));
        mEditText.setText(eText);
        mEditText.requestFocus();
        getDialog().setTitle(eTitle);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        form = getActivity().getLayoutInflater().inflate(jmeFragment.getID(2), null);
        adb.setView(form).
                setPositiveButton(ToolsBase.getTextBase(601), (DialogInterface.OnClickListener) this).
                setNegativeButton(ToolsBase.getTextBase(602), this).
                setTitle("Ti");
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                jmeFragment.jmeapp.stringFromDialog(type, mEditText.getText().toString());
                break;
            case Dialog.BUTTON_NEGATIVE:
                break;
        }
    }
}

