package com.nac.acceleration.filters;

import android.hardware.SensorManager;

/**
 * Created by andreikaralkou on 9/17/13.
 */
public class GravityFilter implements LowPassFilter {
    private float[] output = new float[] {0, 0, 0};


    // Raw accelerometer data
    private float[] gravity = new float[]{
            SensorManager.GRAVITY_EARTH,
            SensorManager.GRAVITY_EARTH,
            SensorManager.GRAVITY_EARTH
    };
    private float alpha = 0.8f;


    @Override
    public float[] addSamples(float[] input) {
        gravity[0] = alpha * gravity[0] + (1 - alpha) * input[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * input[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * input[2];

        output[0] = input[0] - gravity[0];
        output[1] = input[1] - gravity[1];
        output[2] = input[2] - gravity[2];
        return output;
    }

    @Override
    public void setAlphaStatic(boolean alphaStatic) {

    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
