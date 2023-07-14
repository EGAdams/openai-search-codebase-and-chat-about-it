package com.nac.acceleration.filters;

import android.hardware.SensorManager;

/**
 * Created by andreikaralkou on 9/17/13.
 */
public class LPFAndroidDeveloper implements LowPassFilter {
    private boolean alphaStatic = false;

    // Constants for the low-pass filters
    private float timeConstant = 0.18f;
    private float alpha = 0.4f;
    private float dt = 0;

    // Timestamps for the low-pass filters
    private float timestamp = System.nanoTime();
    private float timestampOld = System.nanoTime();

    private int count = 0;

    // Gravity and linear accelerations components for the
    // Wikipedia low-pass filter
    private float[] output = new float[]
            {0, 0, 0};

    // Raw accelerometer data
    private float[] input = new float[]
            {0, 0, 0};

    private float[] gravity = new float[]
            {SensorManager.GRAVITY_EARTH, SensorManager.GRAVITY_EARTH, SensorManager.GRAVITY_EARTH};

    /**
     * Add a sample.
     *
     * @param acceleration The acceleration data.
     * @return Returns the output of the filter.
     */
    public float[] addSamples(float[] acceleration) {
        // Get a local copy of the sensor values
        System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);

        if (!alphaStatic)
        {
            timestamp = System.nanoTime();

            // Find the sample period (between updates).
            // Convert from nanoseconds to seconds
            dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));

            alpha = timeConstant / (timeConstant + dt);

        }

        count++;

        if (count > 5)
        {
            output[0] = alpha * output[0] + (1 - alpha) * input[0];
            output[1] = alpha * output[1] + (1 - alpha) * input[1];
            output[2] = alpha * output[2] + (1 - alpha) * input[2];
        }
//

        return output;
    }

    /**
     * Indicate if alpha should be static.
     *
     * @param alphaStatic A static value for alpha
     */
    public void setAlphaStatic(boolean alphaStatic) {
        this.alphaStatic = alphaStatic;
    }

    /**
     * Set static alpha.
     *
     * @param alpha The value for alpha, 0 < alpha <= 1
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}