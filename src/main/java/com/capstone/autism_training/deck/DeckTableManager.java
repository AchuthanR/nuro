package com.capstone.autism_training.deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.capstone.autism_training.deck.DeckTableHelper;

public class DeckTableManager {

    private DeckTableHelper deckTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public DeckTableManager(Context c) {
        context = c;
    }

    public void open(String table_name) throws SQLException {
        deckTableHelper = new DeckTableHelper(context, table_name);
        database = deckTableHelper.getWritableDatabase();
    }

    public void close() {
        deckTableHelper.close();
    }

    public long insert(String caption, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckTableHelper.CAPTION, caption);
        contentValues.put(DeckTableHelper.IMAGE, image);
        return database.insert(DeckTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DeckTableHelper.ID, DeckTableHelper.CAPTION, DeckTableHelper.IMAGE };
        Cursor cursor = database.query(DeckTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String caption, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckTableHelper.CAPTION, caption);
        contentValues.put(DeckTableHelper.IMAGE, image);
        return database.update(DeckTableHelper.TABLE_NAME, contentValues, DeckTableHelper.ID + " = " + id, null);
    }

    public void delete(long id) {
        database.delete(DeckTableHelper.TABLE_NAME, DeckTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.delete(DeckTableHelper.TABLE_NAME, null, null);
    }
}
