package com.nac.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nac.NacApplication;
import com.nac.R;
import com.nac.configs.MeasureConfig;
import com.nac.ui.activities.ConfigureActivity;

import java.util.Locale;

/**
 * Created by andreikaralkou on 1/16/14.
 */
public class OptionsFragment extends Fragment implements View.OnClickListener, ConfigureActivity.ConfigAction {
    private final static String COUNT_FORMAT = "%.1f";
    private ConfigureActivity parent;
    private RadioButton btnImperial;
    private RadioButton btnMetric;
    private RadioButton btnPercentG;
    private RadioButton btnRcr;
    private EditText edtTargetStoppingSpeed;
    private EditText edtTolerance;
    private EditText edtMinRecordingSpeed;
    private MeasureConfig currentConfig;
    private TextView txtMeasurementUnits1;
    private TextView txtMeasurementUnits2;
    private TextView txtMeasurementUnits3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parent = (ConfigureActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parent = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnImperial = (RadioButton) view.findViewById(R.id.btn_imperial);
        btnImperial.setOnClickListener(this);
        btnMetric = (RadioButton) view.findViewById(R.id.btn_metric);
        btnMetric.setOnClickListener(this);
        btnPercentG = (RadioButton) view.findViewById(R.id.btn_percent_g);
        btnPercentG.setOnClickListener(this);
        btnRcr = (RadioButton) view.findViewById(R.id.btn_rcr);
        btnRcr.setOnClickListener(this);
        txtMeasurementUnits1 = (TextView) view.findViewById(R.id.txt_measurement_units1);
        txtMeasurementUnits2 = (TextView) view.findViewById(R.id.txt_measurement_units2);
        txtMeasurementUnits3 = (TextView) view.findViewById(R.id.txt_measurement_units3);
        edtMinRecordingSpeed = (EditText) view.findViewById(R.id.edt_min_recording_speed);
        edtTargetStoppingSpeed = (EditText) view.findViewById(R.id.edt_target_stopping_speed);
        edtTolerance = (EditText) view.findViewById(R.id.edt_tolerance);
        currentConfig = parent.getConfig();
        updateConfig(currentConfig);
    }

    private void updateConfig(MeasureConfig config) {
        boolean isImperial = config.getMeasurementUnits() == MeasureConfig.MeasurementUnits.IMPERIAL;
        btnImperial.setChecked(isImperial);
        btnMetric.setChecked(!isImperial);
        boolean isPercentG = config.getForceUnits() == MeasureConfig.ForceUnits.PERCENT_G;
        btnPercentG.setChecked(isPercentG);
        btnRcr.setChecked(!isPercentG);
        edtTargetStoppingSpeed.setText(String.format(Locale.US, COUNT_FORMAT, config.getTargetStoppingSpeed()));
        edtTolerance.setText(String.format(Locale.US, COUNT_FORMAT, config.getTolerance()));
        edtMinRecordingSpeed.setText(String.format(Locale.US, COUNT_FORMAT, config.getMinRecordingSpeed()));
        int resId = isImperial ? R.string.mph_label : R.string.kph_label;
        txtMeasurementUnits1.setText(resId);
        txtMeasurementUnits2.setText(resId);
        txtMeasurementUnits3.setText(resId);
        parent.setConfig(config);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_imperial:
                currentConfig.setMeasurementUnits(MeasureConfig.MeasurementUnits.IMPERIAL);
                updateConfig(currentConfig);
                break;
            case R.id.btn_metric:
                currentConfig.setMeasurementUnits(MeasureConfig.MeasurementUnits.METRICS);
                updateConfig(currentConfig);
                break;
            case R.id.btn_percent_g:
                currentConfig.setForceUnits(MeasureConfig.ForceUnits.PERCENT_G);
                updateConfig(currentConfig);
                break;
            case R.id.btn_rcr:
                currentConfig.setForceUnits(MeasureConfig.ForceUnits.RCR);
                updateConfig(currentConfig);
                break;
        }
    }

    @Override
    public MeasureConfig collectConfig() {
        currentConfig.setMinRecordingSpeed(Double.parseDouble(edtMinRecordingSpeed.getText().toString()));
        currentConfig.setTolerance(Double.parseDouble(edtTolerance.getText().toString()));
        currentConfig.setTargetStoppingSpeed(Double.parseDouble(edtTargetStoppingSpeed.getText().toString()));
        currentConfig.setForceUnits(btnPercentG.isChecked() ? MeasureConfig.ForceUnits.PERCENT_G : MeasureConfig.ForceUnits.RCR);
        return currentConfig;
    }

    @Override
    public void resetConfig(MeasureConfig measureConfig) {
        currentConfig = measureConfig;
        updateConfig(currentConfig);
    }
}
