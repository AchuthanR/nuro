package com.capstone.autism_training.card;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.capstone.autism_training.train.SuperMemoTableHelper;

public class DeckTableHelper extends SQLiteOpenHelper {

    public String TABLE_NAME;
    public String SUPERMEMO_TABLE_NAME;

    public static final String ID = "id";
    public static final String IMAGE = "image";
    public static final String CAPTION = "caption";
    public static final String ANSWER = "answer";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    public String CREATE_TABLE;

    public DeckTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = "\"" + table_name.toUpperCase().replace(" ", "_") + "\"";
        SUPERMEMO_TABLE_NAME = "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_") + "\"";
        CREATE_TABLE = createTableQuery(table_name);
    }

    public static String createTableQuery(String table_name) {
        return "CREATE TABLE IF NOT EXISTS " + "\"" + table_name.toUpperCase().replace(" ", "_") + "\"" + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IMAGE + " BLOB NOT NULL, "
                + CAPTION + " TEXT NOT NULL, "
                + ANSWER + " TEXT NOT NULL);";
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
