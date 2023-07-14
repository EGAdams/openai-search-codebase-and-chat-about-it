package com.nac.acceleration.filters;

/**
 * Created by andreikaralkou on 9/16/13.
 */
public class LPFWikipedia implements LowPassFilter
{
    private boolean alphaStatic = true;

    // Constants for the low-pass filters
    private float timeConstant = 0.18f;
    private float alpha = 0.3f;
    private float dt = 0;

    // Timestamps for the low-pass filters
    private float timestamp = System.nanoTime();
    private float timestampOld = System.nanoTime();

    private int count = 0;

    // Gravity and linear accelerations components for the
    // Wikipedia low-pass filter
    private float[] output = new float[]
            { 0, 0, 0 };

    // Raw accelerometer data
    private float[] input = new float[]
            { 0, 0, 0 };

    /**
     * Add a sample.
     *
     * @param acceleration
     *            The acceleration data.
     * @return Returns the output of the filter.
     */
    public float[] addSamples(float[] acceleration)
    {
        // Get a local copy of the sensor values
        System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);

        if (!alphaStatic)
        {
            timestamp = System.nanoTime();

            // Find the sample period (between updates).
            // Convert from nanoseconds to seconds
            dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));

            // Calculate Wikipedia low-pass alpha
            alpha = dt / (timeConstant + dt);

        }

        count++;

        if (count > 5)
        {
            // Update the Wikipedia filter
            // y[i] = y[i] + alpha * (x[i] - y[i])
            output[0] = output[0] + alpha * (this.input[0] - output[0]);
            output[1] = output[1] + alpha * (this.input[1] - output[1]);
            output[2] = output[2] + alpha * (this.input[2] - output[2]);
        }

        return output;
    }

    /**
     * Indicate if alpha should be static.
     *
     * @param alphaStatic
     *            A static value for alpha
     */
    public void setAlphaStatic(boolean alphaStatic)
    {
        this.alphaStatic = alphaStatic;
    }

    /**
     * Set static alpha.
     *
     * @param alpha
     *            The value for alpha, 0 < alpha <= 1
     */
    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
    }
}