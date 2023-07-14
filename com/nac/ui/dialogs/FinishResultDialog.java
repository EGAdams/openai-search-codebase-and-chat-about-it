package com.nac.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nac.R;
import com.nac.model.Run;
import com.nac.model.Test;

import java.util.List;

/**
 * Created by andreikaralkou on 2/5/14.
 */
public class FinishResultDialog extends Dialog implements View.OnClickListener {
    private TextView txtAvg;
    private TextView txt13;
    private TextView txt23;
    private TextView txt33;
    private TextView txtAvgCount;
    private TextView txt13Count;
    private TextView txt23Count;
    private TextView txt33Count;
    private EditText edtOperator;
    private EditText edtOffset;
    private Test test;

    private OnFinishResultListener listener;

    public FinishResultDialog(Context context, Test test, OnFinishResultListener listener) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_finish);
        this.listener = listener;
        findViewById(R.id.btn_accept).setOnClickListener(this);
        findViewById(R.id.btn_reject).setOnClickListener(this);
        txtAvg = (TextView) findViewById(R.id.txt_avg);
        txt13 = (TextView) findViewById(R.id.txt_13);
        txt23 = (TextView) findViewById(R.id.txt_23);
        txt33 = (TextView) findViewById(R.id.txt_33);
        txtAvgCount = (TextView) findViewById(R.id.txt_avg_count);
        txt13Count = (TextView) findViewById(R.id.txt_13_count);
        txt23Count = (TextView) findViewById(R.id.txt_23_count);
        txt33Count = (TextView) findViewById(R.id.txt_33_count);
        edtOperator = (EditText) findViewById(R.id.edt_operator);
        edtOperator.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    edtOperator.setError(null);
                } else {
                    edtOperator.setError(getContext().getString(R.string.operator_offset_error));
                }
            }
        });
        edtOffset = (EditText) findViewById(R.id.edt_offset);
        edtOffset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    edtOffset.setError(null);
                } else {
                    edtOffset.setError(getContext().getString(R.string.operator_offset_error));
                }

            }
        });

        // Calculate test values
        calculateAndSetResult(test);
    }

    private void calculateAndSetResult(Test test) {
        float sum = 0;
        List<Run> runList = test.getRunList();
        int totalCount = runList.size();
        float step13 = 0;
        float step23 = 0;
        float step33 = 0;
        int modulo = totalCount % 3;
        int stepCount = totalCount / 3;
        int count13 = stepCount + (modulo > 0 ? 1 : 0);
        int count23 = stepCount + (modulo > 1 ? 1 : 0);
        int count33 = stepCount;
        for (int i = 0; i < count13; i++) {
            int next = i * 3;
            step13 += runList.get(next).getValue();

            if (++next < totalCount) {
                step23 += runList.get(next).getValue();
            }

            if (++next < totalCount) {
                step33 += runList.get(next).getValue();
            }
        }
        sum = step13 + step23 + step33;

        float avg = 0;
        if (totalCount > 0) {
            avg = sum / totalCount;
        }
        float avg13 = 0;
        float avg23 = 0;
        float avg33 = 0;
        if (count13 > 0) {
            avg13 = step13 / count13;
        }
        if (count23 > 0) {
            avg23 = step23 / count23;
        }
        if (count33 > 0) {
            avg33 = step33 / count33;
        }
        txtAvg.setText(avg > 0 ? String.valueOf(Math.round(avg)) : "?");
        txt13.setText(avg13 > 0 ? String.valueOf(Math.round(avg13)) : "?");
        txt23.setText(avg23 > 0 ? String.valueOf(Math.round(avg23)) : "?");
        txt33.setText(avg33 > 0 ? String.valueOf(Math.round(avg33)) : "?");
        txtAvgCount.setText(String.valueOf(totalCount));
        txt13Count.setText(String.valueOf(count13));
        txt23Count.setText(String.valueOf(count23));
        txt33Count.setText(String.valueOf(count33));
        test.setAverage(avg);
        test.setAverage13(avg13);
        test.setAverage23(avg23);
        test.setAverage33(avg33);
        this.test = test;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                if (!TextUtils.isEmpty(edtOperator.getText()) && !TextUtils.isEmpty(edtOffset.getText())) {
                    if (listener != null) {
                        test.setOperator(edtOperator.getText().toString());
                        test.setOffset(edtOffset.getText().toString());
                        listener.onAcceptTest(test);
                    }
                    dismiss();
                } else {
                    if (TextUtils.isEmpty(edtOperator.getText())) {
                        edtOperator.setError(getContext().getString(R.string.operator_offset_error));
                    } else {
                        edtOperator.setError(null);
                    }
                    if (TextUtils.isEmpty(edtOffset.getText())) {
                        edtOffset.setError(getContext().getString(R.string.operator_offset_error));
                    } else {
                        edtOffset.setError(null);
                    }
                }
                break;
            case R.id.btn_reject:
                if (listener != null) {
                    listener.onRejectTest(this);
                }
                break;
        }

    }

    public static interface OnFinishResultListener {
        void onAcceptTest(Test test);

        void onRejectTest(Dialog dialog);
    }
}
