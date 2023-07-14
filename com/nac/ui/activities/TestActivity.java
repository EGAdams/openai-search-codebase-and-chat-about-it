package com.nac.ui.activities;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.nac.acceleration.AccelerationController;
import com.nac.R;
import com.nac.configs.MeasureConfig;
import com.nac.database.DatabaseHelper;
import com.nac.database.ProgramDataSource;
import com.nac.interfaces.RunStatusChangeListener;
import com.nac.model.Run;
import com.nac.model.RunStatus;
import com.nac.model.Test;
import com.nac.services.SensorService;
import com.nac.ui.dialogs.ConfirmDialog;
import com.nac.ui.dialogs.FinishResultDialog;
import com.nac.ui.dialogs.RunResultDialog;
import com.nac.ui.fragments.ProgramFragment;
import com.nac.ui.views.Speedometer;
import com.nac.utils.Converter;
import com.nac.utils.PreferencesHelper;
import com.nac.utils.SoundPoolHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by andreikaralkou on 1/27/14.
 */
public class TestActivity extends FragmentActivity implements View.OnClickListener, RunStatusChangeListener, RunResultDialog.RunResultDialogListener, FinishResultDialog.OnFinishResultListener {
    // Colors
    private final int greenColor = Color.parseColor("#39b54a");
    private final int redColor = Color.parseColor("#FFFF0000");
    private final int blueColor = Color.parseColor("#ff0099cc");

    private Speedometer speedometer;
    private MeasureConfig config;
    private SensorService service;
    private Button btnNextRun;
    private TextView labelHelpRun;
    private int programId;
    private int testId = -1;
    private Run tempRun;
    private Test tempTest;
    private AccelerationController accelController;
    private TextView avgLabel;
    private TextView testCountLabel;
    private boolean isSoundPlayedInThisRun;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SensorService.LocalBinder binder = (SensorService.LocalBinder) service;
            TestActivity.this.service = binder.getService();
            TestActivity.this.service.addOnActionListener(accelController);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
        }
    };
    private SoundPoolHelper soundPoolHelper;
    private int soundId;

    public static void start(Context context, Bundle programBundle) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.putExtras(programBundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        speedometer = (Speedometer) findViewById(R.id.speedometer);
        tempTest = new Test();
        tempTest.setDate(getCurrentDateTime());
        btnNextRun = (Button) findViewById(R.id.btn_next_run);
        labelHelpRun = (TextView) findViewById(R.id.label_help_run);
        avgLabel = (TextView) findViewById(R.id.test_avg_label);
        testCountLabel = (TextView) findViewById(R.id.test_count_label);
        soundPoolHelper = new SoundPoolHelper(1, this);
        soundId = soundPoolHelper.load(this, R.raw.alarmclock, 1);
        findViewById(R.id.btn_next_run).setOnClickListener(this);
        findViewById(R.id.btn_finish).setOnClickListener(this);
        config = PreferencesHelper.getInstance().getMeasureConfig();
        String avg = "??" + config.getForceUnitsString();
        avgLabel.setText(avg);
        accelController = new AccelerationController(config, speedometer, this);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle programBundle = intent.getExtras();
            if (programBundle != null) {
                programId = programBundle.getInt(DatabaseHelper.COLUMN_ID);
                tempTest.setProgramId(programId);
                programBundle.putBoolean(ProgramFragment.COMPRESS_MODE_EXTRAS, true);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.program_view, ProgramFragment.create(programBundle));
                ft.commit();
            }
        }
    }

    private void playSound() {
        soundPoolHelper.play(soundId);
    }

    private long getCurrentDateTime() {
        return System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SensorService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        service.removeOnActionListener(accelController);
        unbindService(serviceConnection);
    }

    @Override
    protected void onDestroy() {
        soundPoolHelper.release();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_run:
                accelController.nextRun();
                break;
            case R.id.btn_finish:
                new FinishResultDialog(this, tempTest, this).show();
                break;
        }
    }

    @Override
    public void onStatusChanged(RunStatus runStatus) {
        switch (runStatus) {
            case INVALID:
                btnNextRun.setEnabled(false);
                labelHelpRun.setText(R.string.test_help_quite);
                labelHelpRun.setTextColor(greenColor);
                break;
            case READY_TO_START:
                btnNextRun.setEnabled(true);
                labelHelpRun.setText(R.string.test_help_quite);
                labelHelpRun.setTextColor(greenColor);
                break;
            case ACCELERATING:
                isSoundPlayedInThisRun = false;
                labelHelpRun.setText(String.format(Locale.US, getString(R.string.test_accelerate_to), (float) config.getTargetStoppingSpeed(), config.getMeasurementUnitsString()));
                labelHelpRun.setTextColor(blueColor);
                break;
            case START_BRAKING:
                if (!isSoundPlayedInThisRun) {
                    isSoundPlayedInThisRun = true;
                    playSound();
                }
                labelHelpRun.setText(R.string.test_apply_brakes);
                labelHelpRun.setTextColor(greenColor);
                break;
            case TOO_FAST:
                labelHelpRun.setText(String.format(Locale.US, getString(R.string.test_going_fast), (float) config.getTargetStoppingSpeed(), config.getMeasurementUnitsString()));
                labelHelpRun.setTextColor(redColor);
                break;
            case BRAKING:
                float result = accelController.getResultBreakingValue();
                String resultMessage = Math.round(result) + config.getForceUnitsString();
                tempRun = new Run();
                tempRun.setValue(result);
                new RunResultDialog(this, resultMessage, this).show();
                break;
        }
    }

    @Override
    public void onAcceptRun(String condition) {
        tempRun.setCondition(condition);
        tempRun.setDate(getCurrentDateTime());
        tempTest.addRun(tempRun);
        String avg = Math.round(tempTest.calculateAverage()) + config.getForceUnitsString();
        avgLabel.setText(avg);
        testCountLabel.setText(String.format(Locale.US, getString(R.string.test_count_format_label), tempTest.getRunList().size()));
        accelController.reset();
    }

    @Override
    public void onAcceptTest(Test test) {
        int size = test.getRunList().size();
        if (size > 0) {
            ProgramDataSource programDataSource = new ProgramDataSource(this);
            programDataSource.open();
            if (config.getForceUnits() == MeasureConfig.ForceUnits.RCR) {
                // Convert to gravity
                test.setAverage((float)Converter.rcrToGravity(test.getAverage()));
                test.setAverage13((float) Converter.rcrToGravity(test.getAverage13()));
                test.setAverage23((float) Converter.rcrToGravity(test.getAverage23()));
                test.setAverage33((float) Converter.rcrToGravity(test.getAverage33()));
                List<Run> runList = test.getRunList();
                // Convert all test to gravity values
                for (int i = 0; i < size; i++) {
                    runList.get(i).setValue((float) Converter.rcrToGravity(runList.get(i).getValue()));
                }
            }
            // Set condition of first race
            test.setCondition(test.getRunList().get(0).getCondition());
            test.setTestSpeed((float) config.getTargetStoppingSpeed());
            // Save in to database
            programDataSource.createTest(test);
            programDataSource.close();
        }
        finish();
    }

    @Override
    public void onRejectTest(final Dialog dialog) {
        new ConfirmDialog(this, R.string.test_reject_message, new ConfirmDialog.OnConfirmListener() {
            @Override
            public void onConfirm() {
                dialog.dismiss();
                finish();
            }
        }).show();
    }

    @Override
    public void onRejectRace() {
        // Just close dialog
    }
}
