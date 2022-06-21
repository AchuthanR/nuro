package com.technophile.nuro.train;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.technophile.nuro.card.DeckTableHelper;

public class SuperMemoTableManager {

    private SuperMemoTableHelper superMemoTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public SuperMemoTableManager(Context c) {
        context = c;
    }

    public void open(String table_name) throws SQLException {
        superMemoTableHelper = new SuperMemoTableHelper(context, table_name);
        database = superMemoTableHelper.getWritableDatabase();
        database.execSQL(superMemoTableHelper.CREATE_TABLE);
    }

    public void close() {
        if (superMemoTableHelper != null) {
            superMemoTableHelper.close();
        }
    }

    public Cursor fetch() {
        String query = "SELECT a." + DeckTableHelper.ID + ", " + DeckTableHelper.IMAGE + ", "
                + DeckTableHelper.CAPTION + ", " + DeckTableHelper.SHORT_ANSWER + ", "
                + SuperMemoTableHelper.REPETITIONS + ", " + SuperMemoTableHelper.INTERVAL + ", "
                + SuperMemoTableHelper.EASINESS + ", " + SuperMemoTableHelper.NEXT_PRACTICE_TIME
                + " FROM " + superMemoTableHelper.DECK_TABLE_NAME + " a"
                + " INNER JOIN " + superMemoTableHelper.TABLE_NAME + " b"
                + " ON a." + DeckTableHelper.ID + "=b." + SuperMemoTableHelper.ID
                + " WHERE " + SuperMemoTableHelper.NEXT_PRACTICE_TIME + "<" + System.currentTimeMillis();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, int repetitions, int interval, double easiness, long next_practice_time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SuperMemoTableHelper.REPETITIONS, repetitions);
        contentValues.put(SuperMemoTableHelper.INTERVAL, interval);
        contentValues.put(SuperMemoTableHelper.EASINESS, easiness);
        contentValues.put(SuperMemoTableHelper.NEXT_PRACTICE_TIME, next_practice_time);
        return database.update(superMemoTableHelper.TABLE_NAME, contentValues, SuperMemoTableHelper.ID + " = " + id, null);
    }
}
