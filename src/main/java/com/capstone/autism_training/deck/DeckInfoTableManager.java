package com.capstone.autism_training.deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.capstone.autism_training.card.DeckTableHelper;

public class DeckInfoTableManager {

    private DeckInfoTableHelper deckInfoTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public DeckInfoTableManager(Context c) {
        context = c;
    }

    public void open() throws SQLException {
        deckInfoTableHelper = new DeckInfoTableHelper(context);
        database = deckInfoTableHelper.getWritableDatabase();
        database.execSQL(DeckInfoTableHelper.CREATE_TABLE);
    }

    public void close() {
        deckInfoTableHelper.close();
    }

    public long insert(String name, byte[] image, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckInfoTableHelper.NAME, name);
        contentValues.put(DeckInfoTableHelper.IMAGE, image);
        contentValues.put(DeckInfoTableHelper.DESCRIPTION, description);
        return database.insert(DeckInfoTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DeckInfoTableHelper.ID, DeckInfoTableHelper.NAME, DeckInfoTableHelper.IMAGE, DeckInfoTableHelper.DESCRIPTION };
        Cursor cursor = database.query(DeckInfoTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String name, byte[] image, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckInfoTableHelper.NAME, name);
        contentValues.put(DeckInfoTableHelper.IMAGE, image);
        contentValues.put(DeckInfoTableHelper.DESCRIPTION, description);
        return database.update(DeckInfoTableHelper.TABLE_NAME, contentValues, DeckInfoTableHelper.ID + " = " + id, null);
    }

    public void deleteRow(long id) {
        database.delete(DeckInfoTableHelper.TABLE_NAME, DeckInfoTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.delete(DeckInfoTableHelper.TABLE_NAME, null, null);
    }
}
