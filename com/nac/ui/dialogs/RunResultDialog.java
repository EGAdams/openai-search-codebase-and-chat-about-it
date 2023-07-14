package com.nac.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.nac.R;

/**
 * Created by andreikaralkou on 2/5/14.
 */
public class RunResultDialog extends Dialog implements View.OnClickListener {
    private RunResultDialogListener listener;
    private String result;
    private TextView txtResult;
    private Spinner conditionSpinner;

    public RunResultDialog(Context context, String result, RunResultDialogListener listener) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_result);
        this.result = result;
        this.listener = listener;
        txtResult = (TextView) findViewById(R.id.txt_result);
        findViewById(R.id.btn_accept).setOnClickListener(this);
        findViewById(R.id.btn_reject).setOnClickListener(this);
        conditionSpinner = (Spinner) findViewById(R.id.condition_spinner);
        txtResult.setText(result);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                if (listener != null) {
                    listener.onAcceptRun((String) conditionSpinner.getSelectedItem());
                }
                dismiss();
                break;
            case R.id.btn_reject:
                if (listener != null) {
                    listener.onRejectRace();
                }
                dismiss();
                break;
        }
    }

    public static interface RunResultDialogListener {
        void onAcceptRun(String condition);
        void onRejectRace();
    }
}
