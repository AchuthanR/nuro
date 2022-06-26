package com.technophile.nuro.schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleTableHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_PREFIX = "SCHEDULE_";
    public String TABLE_NAME;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String INSTRUCTION = "instruction";
    public static final String START_TIME = "start_time";
    public static final String DURATION = "duration";
    public static final String COMPLETED = "completed";
    public static final String CURRENT_END_TIME = "current_end_time";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    public String CREATE_TABLE;

    public ScheduleTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = "\"" + TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, "
                + IMAGE + " BLOB NOT NULL, "
                + INSTRUCTION + " TEXT, "
                + START_TIME + " INTEGER NOT NULL, "
                + DURATION + " INTEGER NOT NULL, "
                + COMPLETED + " BOOLEAN NOT NULL, "
                + CURRENT_END_TIME + " INTEGER NOT NULL);";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
