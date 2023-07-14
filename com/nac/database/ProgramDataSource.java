package com.nac.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nac.model.Program;
import com.nac.model.Run;
import com.nac.model.Test;

import java.util.List;

/**
 * Created by andreikaralkou on 1/15/14.
 */
public class ProgramDataSource {
    public static final String[] PROGRAM_TABLE_SELECTION = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_AIRPORT_NAME, DatabaseHelper.COLUMN_LOCATION, DatabaseHelper.COLUMN_COMMENT};
    public static final String[] TEST_TABLE_SELECTION = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_RUN_COUNT, DatabaseHelper.COLUMN_AVERAGE, DatabaseHelper.COLUMN_1_3, DatabaseHelper.COLUMN_2_3, DatabaseHelper.COLUMN_3_3, DatabaseHelper.COLUMN_CONDITION, DatabaseHelper.COLUMN_OPERATOR, DatabaseHelper.COLUMN_OFFSET, DatabaseHelper.COLUMN_TEST_SPEED};
    public static final String[] RUN_TABLE_SELECTION = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_CONDITION, DatabaseHelper.COLUMN_VALUE};
    private static final String TEST_LIST_BY_PROGRAM_ID_PATTERN = DatabaseHelper.COLUMN_PROGRAM_ID + "=?";
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ProgramDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public synchronized void createProgram(Program program) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COLUMN_AIRPORT_NAME, program.getAirportName());
        contentValues.put(DatabaseHelper.COLUMN_LOCATION, program.getLocation());
        contentValues.put(DatabaseHelper.COLUMN_COMMENT, program.getComment());
        database.insert(DatabaseHelper.TABLE_PROGRAM, null, contentValues);
    }

    public synchronized Cursor getProgramCursor() {
        return database.query(DatabaseHelper.TABLE_PROGRAM, PROGRAM_TABLE_SELECTION, null, null, null, null, null);
    }

    public synchronized Cursor getTestCursor(int program_id) {
        return database.query(DatabaseHelper.TABLE_TEST, TEST_TABLE_SELECTION, TEST_LIST_BY_PROGRAM_ID_PATTERN, new String[]{String.valueOf(program_id)}, null, null, null);
    }

    public synchronized long createTest(Test test) {
        ContentValues testValues = new ContentValues();
        testValues.put(DatabaseHelper.COLUMN_PROGRAM_ID, test.getProgramId());
        testValues.put(DatabaseHelper.COLUMN_OFFSET, test.getOffset());
        testValues.put(DatabaseHelper.COLUMN_DATE, test.getTestDate());
        testValues.put(DatabaseHelper.COLUMN_AVERAGE, test.getAverage());
        testValues.put(DatabaseHelper.COLUMN_TEST_SPEED, test.getTestSpeed());
        testValues.put(DatabaseHelper.COLUMN_RUN_COUNT, test.getRunList().size());
        testValues.put(DatabaseHelper.COLUMN_OPERATOR, test.getOperator());
        testValues.put(DatabaseHelper.COLUMN_CONDITION, test.getCondition());
        testValues.put(DatabaseHelper.COLUMN_1_3, test.getAverage13());
        testValues.put(DatabaseHelper.COLUMN_2_3, test.getAverage23());
        testValues.put(DatabaseHelper.COLUMN_3_3, test.getAverage33());
        long testId = database.insert(DatabaseHelper.TABLE_TEST, null, testValues);
            database.beginTransaction();
            for (Run run : test.getRunList()) {
                ContentValues runValues = new ContentValues();
                runValues.put(DatabaseHelper.COLUMN_TEST_ID, testId);
                runValues.put(DatabaseHelper.COLUMN_DATE, run.getDate());
                runValues.put(DatabaseHelper.COLUMN_CONDITION, run.getCondition());
                runValues.put(DatabaseHelper.COLUMN_VALUE, run.getValue());
                database.insert(DatabaseHelper.TABLE_RUN, null, runValues);
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        return testId;
    }

    public synchronized void addRaceToTest(long testId, Run run) {
        ContentValues raceValues = new ContentValues();
        raceValues.put(DatabaseHelper.COLUMN_TEST_ID, testId);
        raceValues.put(DatabaseHelper.COLUMN_DATE, run.getDate());
        raceValues.put(DatabaseHelper.COLUMN_CONDITION, run.getCondition());
        raceValues.put(DatabaseHelper.COLUMN_VALUE, run.getValue());
        database.insert(DatabaseHelper.TABLE_RUN, null, raceValues);
    }

    public synchronized boolean isProgramWithIdHasTest(int programId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_TEST, new String[]{DatabaseHelper.COLUMN_ID}, DatabaseHelper.COLUMN_PROGRAM_ID + "=?", new String[] {String.valueOf(programId)}, null, null, null);
        return cursor.getCount() != 0;
    }

    public synchronized void deleteProgramById(int programId) {
        database.delete(DatabaseHelper.TABLE_PROGRAM, DatabaseHelper.COLUMN_ID + "=?", new String[] {String.valueOf(programId)});
    }

    public synchronized Program getProgramById(int programId) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_PROGRAM, PROGRAM_TABLE_SELECTION, DatabaseHelper.COLUMN_ID + "=?", new String[] {String.valueOf(programId)}, null, null, null);
        cursor.moveToFirst();
        String airportName = cursor.getString(1);
        String location = cursor.getString(2);
        String comment = cursor.getString(3);
        return new Program(airportName, location, comment);
    }

    public synchronized Test getTestById(int testId) {
        Test test = new Test();
        Cursor testCursor = database.query(DatabaseHelper.TABLE_TEST, TEST_TABLE_SELECTION, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(testId)}, null, null, null);
        testCursor.moveToFirst();
        test.setDate(testCursor.getLong(1));
        test.setRunCount(testCursor.getInt(2));
        test.setAverage(testCursor.getFloat(3));
        test.setAverage13(testCursor.getFloat(4));
        test.setAverage23(testCursor.getFloat(5));
        test.setAverage33(testCursor.getFloat(6));
        test.setCondition(testCursor.getString(7));
        test.setOperator(testCursor.getString(8));
        test.setOffset(testCursor.getString(9));
        test.setTestSpeed(testCursor.getFloat(10));
        Cursor runCursor = database.query(DatabaseHelper.TABLE_RUN, RUN_TABLE_SELECTION, DatabaseHelper.COLUMN_TEST_ID + "=?", new String[]{String.valueOf(testId)}, null, null, null);
        if (runCursor.moveToFirst()) {
            do {
                Run run = new Run();
                run.setDate(runCursor.getLong(1));
                run.setCondition(runCursor.getString(2));
                run.setValue(runCursor.getFloat(3));
                test.getRunList().add(run);
            } while (runCursor.moveToNext());
        }
        return test;
    }

    public synchronized void deleteTestListByIds(List<Integer> ids) {
        database.beginTransaction();
        for (Integer testId : ids) {
            database.delete(DatabaseHelper.TABLE_TEST, DatabaseHelper.COLUMN_ID + "=?", new String[] {String.valueOf(testId)});
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public synchronized void open() {
        database = databaseHelper.getWritableDatabase();
    }

    public synchronized void close() {
        databaseHelper.close();
    }
}
