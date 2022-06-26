package com.technophile.nuro.deck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectionTableHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_PREFIX = "COLLECTION_";
    public String TABLE_NAME = TABLE_NAME_PREFIX + "GENERAL";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String DESCRIPTION = "description";

    static final String DB_NAME = "database";

    static final int DB_VERSION = 1;

    public final String CREATE_TABLE;

    public CollectionTableHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, "
                + IMAGE + " BLOB NOT NULL, "
                + DESCRIPTION + " TEXT);";
    }

    public CollectionTableHelper(Context context, String table_name) {
        super(context, DB_NAME, null, DB_VERSION);
        TABLE_NAME = TABLE_NAME_PREFIX + table_name.toUpperCase().replace(" ", "_");
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, "
                + IMAGE + " BLOB NOT NULL, "
                + DESCRIPTION + " TEXT);";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
