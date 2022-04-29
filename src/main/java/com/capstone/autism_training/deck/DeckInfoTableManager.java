package com.capstone.autism_training.deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.card.DeckTableManager;

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
        if (deckInfoTableHelper != null) {
            deckInfoTableHelper.close();
        }
    }

    public long insert(String name, byte[] image, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckInfoTableHelper.NAME, name);
        contentValues.put(DeckInfoTableHelper.IMAGE, image);
        contentValues.put(DeckInfoTableHelper.DESCRIPTION, description);
        long rowNumber = database.insert(DeckInfoTableHelper.TABLE_NAME, null, contentValues);

        if (rowNumber != -1) {
            database.execSQL(DeckTableHelper.createTableQuery(name.replace(" ", "_")));
        }

        return rowNumber;
    }

    public Cursor fetch() {
        String[] columns = new String[] { DeckInfoTableHelper.ID, DeckInfoTableHelper.NAME, DeckInfoTableHelper.IMAGE, DeckInfoTableHelper.DESCRIPTION };
        Cursor cursor = database.query(DeckInfoTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String oldName, String name, byte[] image, String description) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckInfoTableHelper.NAME, name);
        contentValues.put(DeckInfoTableHelper.IMAGE, image);
        contentValues.put(DeckInfoTableHelper.DESCRIPTION, description);
        int rowsAffected = database.update(DeckInfoTableHelper.TABLE_NAME, contentValues, DeckInfoTableHelper.ID + " = " + id, null);

        if (!oldName.replace(" ", "_").equals(name.replace(" ", "_"))) {
            database.execSQL("ALTER TABLE " + oldName.replace(" ", "_") + " RENAME TO " + name.replace(" ", "_"));
        }

        return rowsAffected;
    }

    public void deleteRow(long id) {
        String[] columns = new String[] { DeckInfoTableHelper.NAME };
        Cursor cursor = database.query(DeckInfoTableHelper.TABLE_NAME, columns, DeckInfoTableHelper.ID + "=" + id, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            DeckTableManager deckTableManager = new DeckTableManager(context);
            deckTableManager.open(cursor.getString(0).replace(" ", "_"));
            deckTableManager.deleteTable();
            deckTableManager.close();
            database.delete(DeckInfoTableHelper.TABLE_NAME, DeckInfoTableHelper.ID + "=" + id, null);
        }
        cursor.close();
    }

    public void deleteTable() {
        database.execSQL("DROP TABLE IF EXISTS " + DeckInfoTableHelper.TABLE_NAME);
    }
}
