package com.technophile.nuro.train;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.technophile.nuro.card.DeckTableHelper;

public class SuperMemoTableHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_PREFIX = "TRAIN_";
    public String TABLE_NAME;
    public String DECK_TABLE_NAME;

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
        DECK_TABLE_NAME = "\"" + DeckTableHelper.TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
        TABLE_NAME = "\"" + TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
        CREATE_TABLE = createTableQuery(table_name);
    }

    public static String createTableQuery(String table_name) {
        return "CREATE TABLE IF NOT EXISTS " + "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"" + " ("
                + ID + " INTEGER PRIMARY KEY, "
                + REPETITIONS + " INTEGER NOT NULL, "
                + INTERVAL + " INTEGER NOT NULL, "
                + EASINESS + " REAL NOT NULL, "
                + NEXT_PRACTICE_TIME + " INTEGER NOT NULL);";
    }

    public static String deleteTableQuery(String table_name) {
        return "DROP TABLE IF EXISTS " + "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
