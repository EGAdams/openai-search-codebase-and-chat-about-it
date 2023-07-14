package com.nac.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import com.nac.acceleration.filters.LPFWikipedia;
import com.nac.acceleration.filters.LowPassFilter;
import com.nac.acceleration.MovementDirection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreikaralkou on 9/11/13.
 */
public class SensorService extends Service implements SensorEventListener {
    private MovementDirection movementDirection;
    private float timestamp;
    private SensorManager sensorManager;
    private Sensor accelSensor;
    private LowPassFilter kFilter = new LPFWikipedia();
    private List<OnActionListener> listenerList = new ArrayList<OnActionListener>();
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public SensorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Use z negative movement direction by default
        movementDirection = MovementDirection.Z_NEGATIVE;
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        accelSensor = sensors.size() == 0 ? null : sensors.get(0);

        if ((accelSensor != null)) {
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void addOnActionListener(OnActionListener actionListener) {
        listenerList.add(actionListener);
    }

    public void removeOnActionListener(OnActionListener actionListener) {
        listenerList.remove(actionListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if (event.timestamp != 0) {
                    float[] filteredValues = kFilter.addSamples(event.values);
                    updateAcceleration(filteredValues, event.timestamp);
                }
                break;
        }
    }


    private void updateAcceleration(float[] rawValues, long timestamp) {
        for (OnActionListener listener : listenerList) {
            listener.updateAcceleration(rawValues, timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public static interface OnActionListener {
        void updateAcceleration(float[] rawValues, long timestamp);
    }
}
