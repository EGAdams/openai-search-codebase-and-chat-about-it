package com.nac.acceleration.filters;

/**
 * Created by andreikaralkou on 9/16/13.
 */
public interface LowPassFilter
{

    /**
     * Add a sample.
     * @param acceleration The acceleration data.
     * @return Returns the output of the filter.
     */
    public float[] addSamples(float[] acceleration);

    /**
     * Indicate if alpha should be static.
     * @param alphaStatic A static value for alpha
     */
    public void setAlphaStatic(boolean alphaStatic);

    /**
     * Set static alpha.
     * @param alpha The value for alpha, 0 < alpha <= 1
     */
    public void setAlpha(float alpha);

}