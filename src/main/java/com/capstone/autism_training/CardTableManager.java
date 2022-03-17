package com.capstone.autism_training;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CardTableManager {

    private CardTableHelper cardTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public CardTableManager(Context c) {
        context = c;
    }

    public void open(String table_name) throws SQLException {
        cardTableHelper = new CardTableHelper(context, table_name);
        database = cardTableHelper.getWritableDatabase();
    }

    public void close() {
        cardTableHelper.close();
    }

    public long insert(byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardTableHelper.IMAGE, image);
        return database.insert(CardTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { CardTableHelper._ID, CardTableHelper.IMAGE };
        Cursor cursor = database.query(CardTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardTableHelper.IMAGE, image);
        return database.update(CardTableHelper.TABLE_NAME, contentValues, CardTableHelper._ID + " = " + _id, null);
    }

    public void delete(long _id) {
        database.delete(CardTableHelper.TABLE_NAME, CardTableHelper._ID + "=" + _id, null);
    }

    public void deleteTable() {
        database.delete(CardTableHelper.TABLE_NAME, null, null);
    }
}
