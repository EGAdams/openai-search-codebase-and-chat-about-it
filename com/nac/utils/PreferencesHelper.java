package com.nac.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nac.configs.MeasureConfig;

/**
 * Created by andreikaralkou on 1/13/14.
 */
public class PreferencesHelper {
    private static final String MEASUREMENT_UNITS = "com.nac.MEASUREMENT_UNITS";
    private static final String FORCE_UNITS = "com.nac.FORCE_UNITS";
    private static final String MIN_RECORD_SPEED = "com.nac.MIN_RECORD_SPEED";
    private static final String MONITOR_INTERVAL = "com.nac.MONITOR_INTERVAL";
    private static final String TARGET_STOPPING_SPEED = "com.nac.TARGET_STOPPING_SPEED";
    private static final String TOLERANCE = "com.nac.TOLERANCE";
    private static final String ALIGNED_VALUES_X = "com.nac.ALIGNED_VALUES_X";
    private static final String ALIGNED_VALUES_Y = "com.nac.ALIGNED_VALUES_Y";
    private static final String ALIGNED_VALUES_Z = "com.nac.ALIGNED_VALUES_Z";
    private static PreferencesHelper sInstance;
    private Context mContext;

    public static PreferencesHelper getInstance() {
        if (sInstance == null) {
            sInstance = new PreferencesHelper();
        }
        return sInstance;
    }


    public void init(Context context) {
        mContext = context;
    }

    public void saveMeasureConfig(MeasureConfig config) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        putString(editor, MEASUREMENT_UNITS, config.getMeasurementUnits().toString());
        putString(editor, FORCE_UNITS, config.getForceUnits().toString());
        putInt(editor, MONITOR_INTERVAL, config.getMonitorInterval());
        putDouble(editor, TARGET_STOPPING_SPEED, config.getRawTargetStoppingSpeed());
        putDouble(editor, TOLERANCE, config.getRawTolerance());
        putDouble(editor, MIN_RECORD_SPEED, config.getRawMinRecordingSpeed());
        if (config.getAlignedValues() != null) {
            putArrayFloat(editor, config.getAlignedValues());
        }
        editor.commit();
    }

    private void putArrayFloat(SharedPreferences.Editor editor, float[] alignedValues) {
        editor.putFloat(ALIGNED_VALUES_X, alignedValues[0]);
        editor.putFloat(ALIGNED_VALUES_Y, alignedValues[1]);
        editor.putFloat(ALIGNED_VALUES_Z, alignedValues[2]);
    }

    /**
     * Gets MeasureConfig from SharedPreferences or creates from default values if it is not exist.
     */
    public MeasureConfig getMeasureConfig() {
        SharedPreferences sp = getSharedPreferences();
        return new MeasureConfig(
                MeasureConfig.MeasurementUnits.toMeasurementUnits(getString(sp, MEASUREMENT_UNITS, MeasureConfig.MeasurementUnits.IMPERIAL.toString())),
                MeasureConfig.ForceUnits.toForceUnits(getString(sp, FORCE_UNITS, MeasureConfig.ForceUnits.PERCENT_G.toString())),
                getInt(sp, MONITOR_INTERVAL, 25),
                getDouble(sp, TARGET_STOPPING_SPEED, 20),
                getDouble(sp, TOLERANCE, 1),
                getDouble(sp, MIN_RECORD_SPEED, 5),
                getArrayFloat(sp)
        );
    }

    private float[] getArrayFloat(SharedPreferences sp) {
        float[] result = null;
        float x = sp.getFloat(ALIGNED_VALUES_X, -1100f);
        float y = sp.getFloat(ALIGNED_VALUES_Y, -1100f);
        float z = sp.getFloat(ALIGNED_VALUES_Z, -1100f);
        if (x != -1100f) {
            result = new float[3];
            result[0] = x;
            result[1] = y;
            result[2] = z;
        }
        return result;
    }

    public MeasureConfig resetToDefaultMeasureConfig() {
        SharedPreferences sp = getSharedPreferences();
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(MEASUREMENT_UNITS);
        edit.remove(FORCE_UNITS);
        edit.remove(MONITOR_INTERVAL);
        edit.remove(TARGET_STOPPING_SPEED);
        edit.remove(TOLERANCE);
        edit.remove(MIN_RECORD_SPEED);
        edit.remove(ALIGNED_VALUES_X);
        edit.remove(ALIGNED_VALUES_Y);
        edit.remove(ALIGNED_VALUES_Z);
        edit.commit();
        return getMeasureConfig();
    }

    private SharedPreferences getSharedPreferences() {
        checkContext();
        return mContext.getSharedPreferences(PreferencesHelper.class.getName(), Context.MODE_PRIVATE);
    }

    private void checkContext() {
        if (mContext == null) {
            throw new IllegalStateException("Method init in PreferencesHelper should be launch before this method");
        }
    }

    // This is solution to put double value to shared preferences
    private void putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    // This is solution to get double value from shared preferences
    private double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    private void putString(final SharedPreferences.Editor edit, final String key, final String value) {
        edit.putString(key, value);
    }

    private String getString(final SharedPreferences prefs, final String key, final String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    private void putInt(final SharedPreferences.Editor edit, final String key, final int value) {
        edit.putInt(key, value);
    }

    private int getInt(final SharedPreferences prefs, final String key, final int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }
}
