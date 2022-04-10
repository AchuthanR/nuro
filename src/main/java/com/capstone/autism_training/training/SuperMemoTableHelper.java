package com.capstone.autism_training.training;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SuperMemoTableHelper extends SQLiteOpenHelper {

    public static String TABLE_NAME;

    public static final String ID = "id";
    public static final String REPETITIONS = "repetitions";
    public static final String INTERVAL = "interval";
    public static final String EASINESS = "easiness";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    public static String CREATE_TABLE;

    public SuperMemoTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = table_name;
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY, "
                + REPETITIONS + " INTEGER NOT NULL, "
                + INTERVAL + " INTEGER NOT NULL, "
                + EASINESS + " REAL NOT NULL);";
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
