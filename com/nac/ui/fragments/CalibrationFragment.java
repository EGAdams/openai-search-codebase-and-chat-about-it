package com.nac.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nac.R;
import com.nac.configs.MeasureConfig;
import com.nac.services.SensorService;
import com.nac.ui.activities.ConfigureActivity;
import com.nac.utils.Converter;

import java.util.Locale;

/**
 * Created by andreikaralkou on 1/20/14.
 */
public class CalibrationFragment extends Fragment implements SensorService.OnActionListener, View.OnClickListener, ConfigureActivity.ConfigAction {
    private final static String GRAVITY_FORMAT = "%.0f%% g";
    private final static String CALIBRATION_GRAVITY_FORMAT = "%.0f";
    private final static String RCR_FORMAT = "%.0f RCR";

    private TextView labelLiveDataX;
    private TextView labelLiveDataY;
    private TextView labelLiveDataZ;

    private TextView labelLiveCalibrationX;
    private TextView labelLiveCalibrationY;
    private TextView labelLiveCalibrationZ;

    private TextView labelStoredCalibrationX;
    private TextView labelStoredCalibrationY;
    private TextView labelStoredCalibrationZ;

    private SensorService mService;
    private boolean mBound;
    private ConfigureActivity parent;
    private MeasureConfig currentConfig;
    private float[] alignedValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calibration, null);
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
        labelLiveDataX = (TextView) view.findViewById(R.id.txt_live_data_x);
        labelLiveDataY = (TextView) view.findViewById(R.id.txt_live_data_y);
        labelLiveDataZ = (TextView) view.findViewById(R.id.txt_live_data_z);

        labelLiveCalibrationX = (TextView) view.findViewById(R.id.txt_live_calibration_x);
        labelLiveCalibrationY = (TextView) view.findViewById(R.id.txt_live_calibration_y);
        labelLiveCalibrationZ = (TextView) view.findViewById(R.id.txt_live_calibration_z);

        labelStoredCalibrationX = (TextView) view.findViewById(R.id.txt_stored_calibration_x);
        labelStoredCalibrationY = (TextView) view.findViewById(R.id.txt_stored_calibration_y);
        labelStoredCalibrationZ = (TextView) view.findViewById(R.id.txt_stored_calibration_z);
        currentConfig = parent.getConfig();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), SensorService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mService.removeOnActionListener(this);
        getActivity().unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SensorService.LocalBinder binder = (SensorService.LocalBinder) service;
            mService = binder.getService();
            mService.addOnActionListener(CalibrationFragment.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void updateAcceleration(float[] rawValues, long timestamp) {
        boolean isRcr = currentConfig.getForceUnits() == MeasureConfig.ForceUnits.RCR;
        float[] gravityValues = Converter.rawToGravityPercent(rawValues);
        labelLiveDataX.setText(String.format(Locale.US, isRcr ? RCR_FORMAT : GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(rawValues[0]) : gravityValues[0]));
        labelLiveDataY.setText(String.format(Locale.US, isRcr ? RCR_FORMAT : GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(rawValues[1]) : gravityValues[1]));
        labelLiveDataZ.setText(String.format(Locale.US, isRcr ? RCR_FORMAT : GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(rawValues[2]) : gravityValues[2]));

        labelLiveCalibrationX.setText(String.format(Locale.US, CALIBRATION_GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(-rawValues[0]) : -gravityValues[0]));
        labelLiveCalibrationY.setText(String.format(Locale.US, CALIBRATION_GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(-rawValues[1]) : -gravityValues[1]));
        labelLiveCalibrationZ.setText(String.format(Locale.US, CALIBRATION_GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(-rawValues[2]) : -gravityValues[2]));
        float[] storedAlignedValues = currentConfig.getAlignedValues();
        if (storedAlignedValues != null) {
            if(!isRcr) {
                storedAlignedValues = Converter.rawToGravityPercent(storedAlignedValues);
            }
            labelStoredCalibrationX.setText(String.format(Locale.US, CALIBRATION_GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(-storedAlignedValues[0]) : -storedAlignedValues[0]));
            labelStoredCalibrationY.setText(String.format(Locale.US, CALIBRATION_GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(-storedAlignedValues[1]) : -storedAlignedValues[1]));
            labelStoredCalibrationZ.setText(String.format(Locale.US, CALIBRATION_GRAVITY_FORMAT, isRcr ? Converter.rawToRcr(-storedAlignedValues[2]) : -storedAlignedValues[2]));
        }
        alignedValues = new float[3];
        System.arraycopy(rawValues, 0, alignedValues, 0, rawValues.length);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    public void storeCalibration() {
        currentConfig.setAlignedValues(alignedValues);
        labelStoredCalibrationX.setText(labelLiveCalibrationX.getText());
        labelStoredCalibrationY.setText(labelLiveCalibrationY.getText());
        labelStoredCalibrationZ.setText(labelLiveCalibrationZ.getText());
    }

    @Override
    public MeasureConfig collectConfig() {
        return currentConfig;
    }

    @Override
    public void resetConfig(MeasureConfig measureConfig) {
        currentConfig = measureConfig;
        labelStoredCalibrationX.setText("");
        labelStoredCalibrationY.setText("");
        labelStoredCalibrationZ.setText("");
    }
}
