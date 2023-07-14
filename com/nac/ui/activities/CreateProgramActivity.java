package com.nac.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.nac.R;
import com.nac.database.ProgramDataSource;
import com.nac.model.Program;

/**
 * Created by andreikaralkou on 1/15/14.
 */
public class CreateProgramActivity extends Activity implements View.OnClickListener {
    private EditText airportNameText;
    private EditText locationText;
    private EditText commentText;

    private ProgramDataSource dataSource;

    public static void start(Activity context, int requestCode) {
        Intent intent = new Intent(context, CreateProgramActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_program);
        dataSource = new ProgramDataSource(this);
        airportNameText = (EditText) findViewById(R.id.txt_airport_name);
        locationText = (EditText) findViewById(R.id.txt_location);
        commentText = (EditText) findViewById(R.id.txt_comment);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveProgram(
                        airportNameText.getText().toString(),
                        locationText.getText().toString(),
                        commentText.getText().toString()
                );
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    private void saveProgram(String airportName, String location, String comment) {
        dataSource.createProgram(new Program(airportName, location, comment));
    }
}
