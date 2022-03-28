package com.capstone.autism_training.deck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DeckInfoTableHelper extends SQLiteOpenHelper {

    public static String TABLE_NAME = "DECK_INFO";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String DESCRIPTION = "description";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT NOT NULL, "
            + IMAGE + " BLOB NOT NULL, "
            + DESCRIPTION + " TEXT NOT NULL);";

    public DeckInfoTableHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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
