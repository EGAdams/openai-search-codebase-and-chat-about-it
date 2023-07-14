package com.nac.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.nac.R;
import com.nac.adapters.CursorPagerAdapter;
import com.nac.adapters.TestsCursorAdapter;
import com.nac.database.DatabaseHelper;
import com.nac.database.ProgramDataSource;
import com.nac.model.Program;
import com.nac.ui.dialogs.ConfirmDialog;
import com.nac.ui.fragments.ProgramFragment;
import com.nac.ui.views.CircularViewPager;
import com.nac.utils.PreferencesHelper;

import java.util.List;

/**
 * Created by andreikaralkou on 2/10/14.
 */
public class ReviewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private CircularViewPager viewPager;
    private ProgramDataSource dataSource;
    private CursorPagerAdapter<ProgramFragment> pageAdapter;
    private TestsCursorAdapter testsCursorAdapter;
    private ListView testsListView;

    public static void start(Activity context, int programCount, int requestCode) {
        Intent intent = new Intent(context, ReviewActivity.class);
        intent.putExtra("programCount", programCount);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dataSource = new ProgramDataSource(this);
        viewPager = (CircularViewPager) findViewById(R.id.program_view_pager);
        viewPager.setOnPageChangeListener(this);
        testsListView = (ListView) findViewById(R.id.list_tests);
        testsListView.setEmptyView(findViewById(R.id.empty_view));
        findViewById(R.id.btn_previous_program).setOnClickListener(this);
        findViewById(R.id.btn_next_program).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_export).setOnClickListener(this);
        View header = getLayoutInflater().inflate(R.layout.list_tests_header, null);
        testsListView.addHeaderView(header);
        dataSource.open();
        updateProgramList(getIntent().getIntExtra("programCount", 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        if (testsCursorAdapter != null) {
            testsCursorAdapter.renewDateFormat(android.text.format.DateFormat.getDateFormat(getApplicationContext()));
        }
    }

    private void updateProgramList(int count) {
        Cursor cursor = dataSource.getProgramCursor();
        if (cursor.moveToFirst()) {
            int programId = cursor.getInt(0);
            updateTestsAdapterWithProgramId(programId);
        }
        pageAdapter = new CursorPagerAdapter<ProgramFragment>(
                getSupportFragmentManager(),
                ProgramFragment.class,
                ProgramDataSource.PROGRAM_TABLE_SELECTION,
                cursor,
                true
        );
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(count);
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_program:
                nextProgram();
                break;
            case R.id.btn_previous_program:
                previousProgram();
                break;
            case R.id.btn_delete:
                deleteSelectedTests();
                break;
            case R.id.btn_export:
                exportSelectedTests();
                break;
        }
    }

    private void exportSelectedTests() {
        if (testsCursorAdapter != null) {
            List<Integer> ids = testsCursorAdapter.getSelectedTestsIds();
            if (ids.size() > 0) {
                int position = viewPager.getCurrentRealItem();
                Bundle bundle = pageAdapter.getBundleForCursorPosition(position);
                Program program = new Program(bundle.getString(DatabaseHelper.COLUMN_AIRPORT_NAME), bundle.getString(DatabaseHelper.COLUMN_LOCATION), bundle.getString(DatabaseHelper.COLUMN_COMMENT));
                ReportActivity.start(this, program, testsCursorAdapter.getSelectedTestsIds());
            } else {
                Toast.makeText(this, R.string.nothing_export, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.nothing_export, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSelectedTests() {
        if (testsCursorAdapter != null) {
            final List<Integer> ids = testsCursorAdapter.getSelectedTestsIds();
            if (ids != null && ids.size() > 0) {
                new ConfirmDialog(this, R.string.review_dialog_delete_tests, R.string.yes, R.string.no, new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        dataSource.deleteTestListByIds(ids);
                        updateTestsAdapterWithProgramId(getCurrentProgramId());
                    }
                }).show();
            } else {
                Toast.makeText(this, R.string.nothing_delete, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.nothing_delete, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("programCount", viewPager.getCurrentRealItem());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private int getCurrentProgramId() {
        int position = viewPager.getCurrentRealItem();
        return pageAdapter.getProgramIdForCursorPosition(position);
    }

    private void previousProgram() {
        viewPager.setCurrentItem(getItem(-1), false);
    }

    private void nextProgram() {
        viewPager.forceSetCurrentItem(getItem(+1), false);
    }

    private int getItem(int i) {
        int a = viewPager.getCurrentItem();
        i += a;
        return i;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        int programId = pageAdapter.getProgramIdForPosition(i);
        updateTestsAdapterWithProgramId(programId);
    }

    private void updateTestsAdapterWithProgramId(int programId) {
        if (testsCursorAdapter == null) {
            testsCursorAdapter = new TestsCursorAdapter(this, dataSource.getTestCursor(programId), false, PreferencesHelper.getInstance().getMeasureConfig());
            testsListView.setAdapter(testsCursorAdapter);
        } else {
            testsCursorAdapter.swapCursor(dataSource.getTestCursor(programId));
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
