package com.nac.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nac.R;

/**
 * Created by andreikaralkou on 2/6/14.
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private OnConfirmListener listener;

    public ConfirmDialog(Context context, int messageResId, OnConfirmListener listener) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_confirm);
        this.listener = listener;
        ((TextView) findViewById(R.id.label_message)).setText(messageResId);
        findViewById(R.id.btn_accept).setOnClickListener(this);
        findViewById(R.id.btn_reject).setOnClickListener(this);
    }

    public ConfirmDialog(Context context, int messageResId, int positiveResId, int negativeResId, OnConfirmListener listener) {
        this(context, messageResId, listener);
        ((Button) findViewById(R.id.btn_accept)).setText(positiveResId);
        ((Button) findViewById(R.id.btn_reject)).setText(negativeResId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                if (listener != null) {
                    listener.onConfirm();
                }
                dismiss();
                break;
            case R.id.btn_reject:
                dismiss();
                break;
        }
    }

    public static interface OnConfirmListener {
        void onConfirm();
    }
}
