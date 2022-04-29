package com.capstone.autism_training.card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.capstone.autism_training.training.SuperMemoTableHelper;

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
        database.execSQL(deckTableHelper.CREATE_TABLE);

        database.execSQL(SuperMemoTableHelper.createTableQuery(table_name));
    }

    public void close() {
        if (deckTableHelper != null) {
            deckTableHelper.close();
        }
    }

    public long insert(byte[] image, String caption, String answer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckTableHelper.IMAGE, image);
        contentValues.put(DeckTableHelper.CAPTION, caption);
        contentValues.put(DeckTableHelper.ANSWER, answer);
        long rowNumber = database.insert(deckTableHelper.TABLE_NAME, null, contentValues);

        if (rowNumber != -1) {
            contentValues = new ContentValues();
            contentValues.put(SuperMemoTableHelper.ID, rowNumber);
            contentValues.put(SuperMemoTableHelper.REPETITIONS, 0);
            contentValues.put(SuperMemoTableHelper.INTERVAL, 0);
            contentValues.put(SuperMemoTableHelper.EASINESS, 2.5);
            contentValues.put(SuperMemoTableHelper.NEXT_PRACTICE_TIME, System.currentTimeMillis());
            database.insert(deckTableHelper.SUPERMEMO_TABLE_NAME, null, contentValues);
        }

        return rowNumber;
    }

    public Cursor fetch() {
        String[] columns = new String[] { DeckTableHelper.ID, DeckTableHelper.IMAGE, DeckTableHelper.CAPTION, DeckTableHelper.ANSWER };
        Cursor cursor = database.query(deckTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, byte[] image, String caption, String answer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DeckTableHelper.IMAGE, image);
        contentValues.put(DeckTableHelper.CAPTION, caption);
        contentValues.put(DeckTableHelper.ANSWER, answer);
        int rowsAffected = database.update(deckTableHelper.TABLE_NAME, contentValues, DeckTableHelper.ID + " = " + id, null);

        if (rowsAffected > 0) {
            contentValues = new ContentValues();
            contentValues.put(SuperMemoTableHelper.REPETITIONS, 0);
            contentValues.put(SuperMemoTableHelper.INTERVAL, 0);
            contentValues.put(SuperMemoTableHelper.EASINESS, 2.5);
            contentValues.put(SuperMemoTableHelper.NEXT_PRACTICE_TIME, System.currentTimeMillis());
            database.update(deckTableHelper.SUPERMEMO_TABLE_NAME, contentValues, SuperMemoTableHelper.ID + "=" + id, null);
        }

        return rowsAffected;
    }

    public void deleteRow(long id) {
        database.delete(deckTableHelper.TABLE_NAME, DeckTableHelper.ID + "=" + id, null);
        database.delete(deckTableHelper.SUPERMEMO_TABLE_NAME, SuperMemoTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.execSQL("DROP TABLE IF EXISTS " + deckTableHelper.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + deckTableHelper.SUPERMEMO_TABLE_NAME);
    }
}
