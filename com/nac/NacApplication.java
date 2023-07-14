package com.nac;

import android.app.Application;

import com.nac.configs.MeasureConfig;
import com.nac.utils.PreferencesHelper;

import java.text.Format;

/**
 * Created by andreikaralkou on 1/14/14.
 */
public class NacApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Init preferences helper
        PreferencesHelper preferencesHelper = PreferencesHelper.getInstance();
        preferencesHelper.init(this);
    }
}
