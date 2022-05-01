package com.capstone.autism_training.deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.capstone.autism_training.card.DeckTableHelper;
import com.capstone.autism_training.card.DeckTableManager;
import com.capstone.autism_training.train.SuperMemoTableHelper;

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
        try {
            database.execSQL(DeckTableHelper.createTableQuery(name));
        }
        catch (SQLiteException exception) {
            return -1;
        }

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

    public int update(long id, String oldName, String name, byte[] image, String description) {
        try {
            if (!oldName.toUpperCase().replace(" ", "_").equals(name.toUpperCase().replace(" ", "_"))) {
                database.execSQL("ALTER TABLE " + "\"" + oldName.toUpperCase().replace(" ", "_") + "\"" + " RENAME TO " + "\"" + name.toUpperCase().replace(" ", "_") + "\"");
                database.execSQL("ALTER TABLE " + "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + oldName.toUpperCase().replace(" ", "_") + "\"" + " RENAME TO " + "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + name.toUpperCase().replace(" ", "_") + "\"");
            }
        }
        catch (SQLiteException exception) {
            return 0;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckInfoTableHelper.NAME, name);
        contentValues.put(DeckInfoTableHelper.IMAGE, image);
        contentValues.put(DeckInfoTableHelper.DESCRIPTION, description);
        return database.update(DeckInfoTableHelper.TABLE_NAME, contentValues, DeckInfoTableHelper.ID + " = " + id, null);
    }

    public void deleteRow(long id) {
        String[] columns = new String[] { DeckInfoTableHelper.NAME };
        Cursor cursor = database.query(DeckInfoTableHelper.TABLE_NAME, columns, DeckInfoTableHelper.ID + "=" + id, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            DeckTableManager deckTableManager = new DeckTableManager(context);
            deckTableManager.open(cursor.getString(0));
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
