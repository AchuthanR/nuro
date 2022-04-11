package com.capstone.autism_training.visual_schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VisualScheduleTableHelper extends SQLiteOpenHelper {

    public String TABLE_NAME;

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String INSTRUCTION = "instruction";
    public static final String START_TIME = "start_time";
    public static final String DURATION = "duration";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    public String CREATE_TABLE;

    public VisualScheduleTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = table_name;
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, "
                + IMAGE + " BLOB NOT NULL, "
                + INSTRUCTION + " TEXT NOT NULL, "
                + START_TIME + " INTEGER NOT NULL, "
                + DURATION + " INTEGER NOT NULL);";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
