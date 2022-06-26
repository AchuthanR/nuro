package com.technophile.nuro.card;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.technophile.nuro.train.SuperMemoTableHelper;

public class DeckTableHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_PREFIX = "DECK_";
    public String TABLE_NAME;
    public String SUPERMEMO_TABLE_NAME;

    public static final String ID = "id";
    public static final String IMAGE = "image";
    public static final String CAPTION = "caption";
    public static final String SHORT_ANSWER = "short_answer";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    public String CREATE_TABLE;

    public DeckTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = "\"" + TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
        SUPERMEMO_TABLE_NAME = "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
        CREATE_TABLE = createTableQuery(table_name);
    }

    public static String createTableQuery(String table_name) {
        return "CREATE TABLE IF NOT EXISTS " + "\"" + TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"" + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IMAGE + " BLOB NOT NULL, "
                + CAPTION + " TEXT NOT NULL, "
                + SHORT_ANSWER + " TEXT NOT NULL);";
    }

    public static String deleteTableQuery(String table_name) {
        return "DROP TABLE IF EXISTS " + "\"" + DeckTableHelper.TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
