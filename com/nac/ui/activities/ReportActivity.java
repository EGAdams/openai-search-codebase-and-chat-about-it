package com.nac.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.nac.R;
import com.nac.configs.MeasureConfig;
import com.nac.database.ProgramDataSource;
import com.nac.model.Program;
import com.nac.ui.dialogs.WarningDialog;
import com.nac.utils.AsyncTaskResult;
import com.nac.utils.DeviceUuidFactory;
import com.nac.utils.PreferencesHelper;
import com.nac.utils.ReportGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by andreikaralkou on 2/12/14.
 */
public class ReportActivity extends Activity implements View.OnClickListener {
    private final static String PROGRAM_EXTRAS = "com.nac.PROGRAM_EXTRAS";
    private final static String IDS_EXTRAS = "com.nac.IDS_EXTRAS";
    private ArrayList<Integer> testIds;
    private Program program;
    private WebView webView;
    private TextView positionText;
    private ProgramDataSource dataSource;
    private String UUID;
    private MeasureConfig config;
    private int currentIdCount;
    private int size;
    private List<String> reports;
    private ProgressDialog progressDialog;
    private Format dateFormat;
    private Format timeFormat;

    public static void start(Context context, Program program, List<Integer> ids) {
        Intent intent = new Intent(context, ReportActivity.class);
        intent.putIntegerArrayListExtra(IDS_EXTRAS, (ArrayList<Integer>) ids);
        intent.putExtra(PROGRAM_EXTRAS, program);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_report);
        webView = (WebView) findViewById(R.id.report_web_view);
        positionText = (TextView) findViewById(R.id.txt_position);
        dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        timeFormat = new SimpleDateFormat(
                getString(R.string.time_long_format), Locale.getDefault());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        dataSource = new ProgramDataSource(this);
        dataSource.open();
        UUID = new DeviceUuidFactory(this).getDeviceUuid().toString();
        config = PreferencesHelper.getInstance().getMeasureConfig();
        Intent intent = getIntent();
        if (intent != null) {
            program = (Program) intent.getSerializableExtra(PROGRAM_EXTRAS);
            testIds = intent.getIntegerArrayListExtra(IDS_EXTRAS);
        }
        findViewById(R.id.btn_previous_program).setOnClickListener(this);
        findViewById(R.id.btn_print).setOnClickListener(this);
        findViewById(R.id.btn_next_program).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        currentIdCount = 0;
        size = testIds.size();
        if (testIds.size() == 1) {
            findViewById(R.id.btn_next_program).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_previous_program).setVisibility(View.INVISIBLE);
            positionText.setVisibility(View.GONE);
        } else {
            updatePositionText();
        }
        generateReports();
    }

    private void updatePositionText() {
        positionText.setText(String.format("%d/%d", currentIdCount + 1, size));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    private void updateWebViewWithReport(String report) {
        webView.loadData(report, "text/html; charset=UTF-8", null);
    }

    private void generateReports() {
        new AsyncTask<Void, Void, AsyncTaskResult<List<String>>>() {

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(
                        ReportActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getString(R.string.report_wait));
                progressDialog.show();
            }

            @Override
            protected AsyncTaskResult<List<String>> doInBackground(Void... voids) {
                String uri = Environment.getExternalStorageDirectory() + "/NAC_reports";
                File folder = new File(uri);
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                List<String> reports = new ArrayList<String>(testIds.size());
                for (Integer id : testIds) {
                    String report = ReportGenerator.generateReport(UUID, config, program, dataSource.getTestById(id), dateFormat, timeFormat);
                    reports.add(report);
                    if (success) {
                        File file = new File(folder, id + ".html");
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                                FileOutputStream fout = new FileOutputStream(file);
                                OutputStreamWriter osw = new OutputStreamWriter(fout);
                                osw.write(report);
                                osw.flush();
                                osw.close();
                            } catch (FileNotFoundException e) {
                                success = false;
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                                success = false;
                            }
                        }
                    }
                }
                if (!success) {
                    return new AsyncTaskResult<List<String>>(reports, new Exception());
                }
                return new AsyncTaskResult<List<String>>(reports);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<String>> result) {
                progressDialog.dismiss();
                progressDialog = null;
                if (result.getException() != null) {
                    new WarningDialog(ReportActivity.this, R.string.report_save_warning, null).show();
                } else {
                    new WarningDialog(ReportActivity.this, R.string.report_save_completed, null).show();
                }
                reports = result.getResult();
                updateWebViewWithReport(reports.get(0));
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_program:
                if (size - 1 > currentIdCount && reports != null) {
                    updateWebViewWithReport(reports.get(++currentIdCount));
                    updatePositionText();
                }
                break;
            case R.id.btn_previous_program:
                if (currentIdCount > 0 && reports != null) {
                    updateWebViewWithReport(reports.get(--currentIdCount));
                    updatePositionText();
                }
                break;
            case R.id.btn_print:
                printCurrentDocument();
                break;
            case R.id.btn_exit:
                finish();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void printCurrentDocument() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

            // Get a print adapter instance
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

            // Create a print job with name and adapter instance
            String jobName = getString(R.string.app_name) + " Document";
            printManager.print(jobName, printAdapter,
                    new PrintAttributes.Builder().build());
        } else {
            Toast.makeText(this, "This function not supported by this device. Please update to Android 4.4 (KitKat).", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }
}
