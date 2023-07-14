package com.nac.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andreikaralkou on 1/14/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_PROGRAM = "table_program";
    public static final String TABLE_TEST = "table_test";
    public static final String TABLE_RUN = "table_run";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AIRPORT_NAME = "airport_name";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_PROGRAM_ID = "program_id";
    public static final String COLUMN_CONDITION = "condition";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_AVERAGE = "average";
    public static final String COLUMN_DEVICE_ID = "device_id";
    public static final String COLUMN_OPERATOR = "operator";
    public static final String COLUMN_OFFSET = "offset";
    public static final String COLUMN_TEST_SPEED = "test_speed";
    public static final String COLUMN_1_3 = "column_1_3";
    public static final String COLUMN_2_3 = "column_2_3";
    public static final String COLUMN_3_3 = "column_3_3";
    public static final String COLUMN_TEST_ID = "test_id";
    public static final String COLUMN_RUN_COUNT = "run_count";

    private static final String DATABASE_NAME = "nac_database.db";
    private static final int DATABASE_VERSION = 5;

    private static final String PROGRAM_CREATE = "create table " +
            TABLE_PROGRAM + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_AIRPORT_NAME + " text, " +
            COLUMN_LOCATION + " text, " +
            COLUMN_COMMENT + " text);";

    private static final String TEST_CREATE = "create table " +
            TABLE_TEST + "(" +
            COLUMN_ID + " INTEGER primary key autoincrement, " +
            COLUMN_DATE + " INTEGER, " +
            COLUMN_DEVICE_ID + " TEXT, " +
            COLUMN_OPERATOR + " TEXT, " +
            COLUMN_CONDITION + " TEXT, " +
            COLUMN_OFFSET + " TEXT, " +
            COLUMN_AVERAGE + " FLOAT, " +
            COLUMN_TEST_SPEED + " TEXT, " +
            COLUMN_1_3 + " FLOAT, " +
            COLUMN_2_3 + " FLOAT, " +
            COLUMN_3_3 + " FLOAT, " +
            COLUMN_RUN_COUNT + " INTEGER, " +
            COLUMN_PROGRAM_ID + " INTEGER);";

    private static final String RUN_CREATE = "create table " +
            TABLE_RUN + "(" +
            COLUMN_ID + " INTEGER primary key autoincrement, " +
            COLUMN_TEST_ID + " INTEGER, " +
            COLUMN_DATE + " INTEGER, " +
            COLUMN_CONDITION + " TEXT, " +
            COLUMN_VALUE + " FLOAT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PROGRAM_CREATE);
        db.execSQL(TEST_CREATE);
        db.execSQL(RUN_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST);
        onCreate(db);
    }
}
