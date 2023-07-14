package com.nac.configs;

import com.nac.utils.Converter;

/**
 * Created by andreikaralkou on 1/13/14.
 */
public class MeasureConfig {

    public float[] getAlignedValues() {
        return alignedValues;
    }

    public void setAlignedValues(float[] alignedValues) {
        this.alignedValues = alignedValues;
    }

    public static enum ForceUnits {
        PERCENT_G, RCR;

        public static ForceUnits toForceUnits(String forceUnit) {
            try {
                return valueOf(forceUnit);
            } catch (Exception ex) {
                throw new IllegalStateException("Invalid ForceUnits params");
            }
        }
    }

    public static enum MeasurementUnits {
        IMPERIAL, METRICS;

        public static MeasurementUnits toMeasurementUnits(String measurementUnit) {
            try {
                return valueOf(measurementUnit);
            } catch (Exception ex) {
                throw new IllegalStateException("Invalid MeasurementUnits params");
            }
        }
    }

    private MeasurementUnits measurementUnits;

    private ForceUnits forceUnits;

    private int monitorInterval;

    // Stored in imperial values
    private double targetStoppingSpeed;

    // Stored in imperial values
    private double tolerance;

    // Stored in imperial values
    private double minRecordingSpeed;

    private float[] alignedValues;

    public MeasureConfig(MeasurementUnits measurementUnits, ForceUnits forceUnits, int monitorInterval, double targetStoppingSpeed, double tolerance, double minRecordingSpeed) {
        this.measurementUnits = measurementUnits;
        this.forceUnits = forceUnits;
        this.monitorInterval = monitorInterval;
        this.targetStoppingSpeed = targetStoppingSpeed;
        this.tolerance = tolerance;
        this.minRecordingSpeed = minRecordingSpeed;
    }

    public MeasureConfig(MeasurementUnits measurementUnits, ForceUnits forceUnits, int monitorInterval, double targetStoppingSpeed, double tolerance, double minRecordingSpeed, float[] alignedValues) {
        this(measurementUnits, forceUnits, monitorInterval, targetStoppingSpeed, tolerance, minRecordingSpeed);
        this.alignedValues = alignedValues;
    }

    public MeasurementUnits getMeasurementUnits() {
        return measurementUnits;
    }

    public String getMeasurementUnitsString() {
        if (measurementUnits == MeasureConfig.MeasurementUnits.IMPERIAL) {
            return "mph";
        } else {
            return "kph";
        }
    }

    public void setMeasurementUnits(MeasurementUnits measurementUnits) {
        this.measurementUnits = measurementUnits;
    }

    public ForceUnits getForceUnits() {
        return forceUnits;
    }

    public String getForceUnitsString() {
        if (forceUnits == ForceUnits.PERCENT_G) {
            return "% g";
        } else {
            return "RCR";
        }
    }

    public void setForceUnits(ForceUnits forceUnits) {
        this.forceUnits = forceUnits;
    }

    public int getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(int monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    public double getTargetStoppingSpeed() {
        if (measurementUnits == MeasurementUnits.METRICS) {
            return Converter.milesToKm(targetStoppingSpeed);
        } else {
            return targetStoppingSpeed;
        }
    }

    public double getRawTargetStoppingSpeed() {
        return targetStoppingSpeed;
    }

    public void setTargetStoppingSpeed(double targetStoppingSpeed) {
        if (measurementUnits == MeasurementUnits.METRICS) {
            this.targetStoppingSpeed = Converter.kmToMiles(targetStoppingSpeed);
        } else {
            this.targetStoppingSpeed = targetStoppingSpeed;
        }
    }

    public double getTolerance() {
        if (measurementUnits == MeasurementUnits.METRICS) {
            return Converter.milesToKm(tolerance);
        } else {
            return tolerance;
        }
    }

    public double getRawTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        if (measurementUnits == MeasurementUnits.METRICS) {
            this.tolerance = Converter.kmToMiles(tolerance);
        } else {
            this.tolerance = tolerance;
        }
    }

    public double getMinRecordingSpeed() {
        if (measurementUnits == MeasurementUnits.METRICS) {
            return Converter.milesToKm(minRecordingSpeed);
        } else {
            return minRecordingSpeed;
        }
    }

    public double getRawMinRecordingSpeed() {
        return minRecordingSpeed;
    }

    public void setMinRecordingSpeed(double minRecordingSpeed) {
        if (measurementUnits == MeasurementUnits.METRICS) {
            this.minRecordingSpeed = Converter.kmToMiles(minRecordingSpeed);
        } else {
            this.minRecordingSpeed = minRecordingSpeed;
        }
    }
}
