package com.capstone.autism_training.deck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DeckTableHelper extends SQLiteOpenHelper {

    public static String TABLE_NAME;

    public static final String ID = "id";
    public static final String CAPTION = "caption";
    public static final String IMAGE = "image";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    private static String CREATE_TABLE;

    public DeckTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = table_name;
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CAPTION + " TEXT NOT NULL, "
                + IMAGE + " BLOB NOT NULL);";
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