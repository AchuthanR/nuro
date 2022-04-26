package com.capstone.autism_training.training;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SuperMemoTableHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_PREFIX = "TRAIN_";
    public String TABLE_NAME;

    public static final String ID = "id";
    public static final String REPETITIONS = "repetitions";
    public static final String INTERVAL = "interval";
    public static final String EASINESS = "easiness";
    public static final String NEXT_PRACTICE_TIME = "next_practice_time";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    public String CREATE_TABLE;

    public SuperMemoTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = TABLE_NAME_PREFIX + table_name;
        CREATE_TABLE = createTableQuery(TABLE_NAME);
    }

    public static String createTableQuery(String TABLE_NAME) {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY, "
                + REPETITIONS + " INTEGER NOT NULL, "
                + INTERVAL + " INTEGER NOT NULL, "
                + EASINESS + " REAL NOT NULL, "
                + NEXT_PRACTICE_TIME + " INTEGER NOT NULL);";
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
