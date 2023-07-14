package com.nac.utils;

import android.hardware.SensorManager;

/**
 * Created by andreikaralkou on 1/16/14.
 */
public class Converter {
    private final static double MILS_COEF = 1.609344f;
    private final static double RCR_COEF = 32.1737f;

    public static double milesToKm(double miles) {
        return miles * MILS_COEF;
    }

    public static double kmToMiles(double km) {
        return km / MILS_COEF;
    }

    public static double rawToRcr(double raw) {
        return raw / SensorManager.GRAVITY_EARTH * RCR_COEF;
    }

    public static double rcrToRaw(double rcr) {
        return rcr / RCR_COEF * SensorManager.GRAVITY_EARTH;
    }

    public static double rcrToGravity(double rcr) {
        return rcr * 100f / RCR_COEF;
    }

    public static double gravityToRaw(double gravity) {
        return gravity * SensorManager.GRAVITY_EARTH / 100f;
    }

    public static double gravityToRcr(double gravity) {
        return gravity / 100f * RCR_COEF;
    }

    public static float[] rawToGravityPercent(float[] values) {
        float[] gravityValues = new float[3];
        System.arraycopy(values, 0, gravityValues, 0, gravityValues.length);
        for (int i = 0; i < gravityValues.length; i++) {
            gravityValues[i] = (gravityValues[i] / SensorManager.GRAVITY_EARTH) * 100f;
        }
        return gravityValues;
    }

    public static float[] rawToRcr(float[] values) {
        float result[] = new float[3];
        System.arraycopy(values, 0, result, 0, result.length);
        for (int i = 0; i < result.length; i++) {
            result[i] = (float) rawToRcr(result[i]);
        }
        return result;
    }
}
