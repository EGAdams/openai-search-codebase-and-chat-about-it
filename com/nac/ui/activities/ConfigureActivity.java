package com.nac.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.nac.NacApplication;
import com.nac.R;
import com.nac.configs.MeasureConfig;
import com.nac.ui.fragments.CalibrationFragment;
import com.nac.ui.fragments.OptionsFragment;
import com.nac.utils.PreferencesHelper;

/**
 * Created by andreikaralkou on 1/14/14.
 */
public class ConfigureActivity extends FragmentActivity implements TabHost.OnTabChangeListener, View.OnClickListener {
    public static final String TAB_OPTIONS = "options";
    public static final String TAB_CALIBRATION = "calibration";

    private TabHost tabHost;
    private int currentTab;
    private String currentTabId;
    private Button alignButton;

    private MeasureConfig currentConfig;

    public static void start(Context context) {
        Intent launchIntent = new Intent(context, ConfigureActivity.class);
        context.startActivity(launchIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_configure);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_defaults).setOnClickListener(this);
        alignButton = (Button) findViewById(R.id.btn_align);
        alignButton.setOnClickListener(this);
        setupTabs();
        getStoredConfig();
    }

    private void setupTabs() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(); // you must call this before adding your tabs!
        tabHost.addTab(newTab(TAB_OPTIONS, R.string.options_tab, R.id.tab_options));
        tabHost.addTab(newTab(TAB_CALIBRATION, R.string.calibration_tab, R.id.tab_calibration));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabHost.setOnTabChangedListener(this);
        tabHost.setCurrentTab(currentTab);
        onTabChanged(TAB_OPTIONS);
    }

    private TabHost.TabSpec newTab(String tag, int labelId, int tabContentId) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setContent(tabContentId);
        View tabLayout = getLayoutInflater().inflate(R.layout.tab, null);
        ((TextView) tabLayout.findViewById(R.id.text)).setText(labelId);
        tabSpec.setIndicator(tabLayout);
        return tabSpec;
    }

    private MeasureConfig getStoredConfig() {
        currentConfig = PreferencesHelper.getInstance().getMeasureConfig();
        return currentConfig;
    }

    @Override
    public void onTabChanged(String tabId) {
        currentTabId = tabId;
        if (TAB_OPTIONS.equals(tabId)) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag(tabId) == null) {
                fm.beginTransaction()
                        .replace(R.id.tab_options, new OptionsFragment(), tabId)
                        .commit();
            }
            currentTab = 0;
            alignButton.setVisibility(View.INVISIBLE);
            return;
        }
        if (TAB_CALIBRATION.equals(tabId)) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag(tabId) == null) {
                fm.beginTransaction()
                        .replace(R.id.tab_calibration, new CalibrationFragment(), tabId)
                        .commit();
            }
            currentTab = 1;
            alignButton.setVisibility(View.VISIBLE);
            return;
        }
    }

    public void setConfig(MeasureConfig config) {
        currentConfig = config;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                collectAndSave();
                finish();
                break;
            case R.id.btn_defaults:
                setDefaultConfig();
                break;
            case R.id.btn_align:
                ((CalibrationFragment) getSupportFragmentManager().findFragmentByTag(TAB_CALIBRATION)).storeCalibration();
                break;
        }
    }

    public MeasureConfig getConfig() {
        return currentConfig;
    }

    private void setDefaultConfig() {
        currentConfig = PreferencesHelper.getInstance().resetToDefaultMeasureConfig();
        ((ConfigAction) getSupportFragmentManager().findFragmentByTag(currentTabId)).resetConfig(currentConfig);
    }

    private void collectAndSave() {
        currentConfig = ((ConfigAction) getSupportFragmentManager().findFragmentByTag(currentTabId)).collectConfig();
        PreferencesHelper.getInstance().saveMeasureConfig(currentConfig);
    }

    public static interface ConfigAction {
        MeasureConfig collectConfig();
        void resetConfig(MeasureConfig measureConfig);
    }
}
