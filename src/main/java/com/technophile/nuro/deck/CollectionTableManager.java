package com.technophile.nuro.deck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.technophile.nuro.card.DeckTableHelper;
import com.technophile.nuro.train.SuperMemoTableHelper;

public class CollectionTableManager {

    private CollectionTableHelper collectionTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public CollectionTableManager(Context c) {
        context = c;
    }

    public void open() throws SQLException {
        collectionTableHelper = new CollectionTableHelper(context);
        database = collectionTableHelper.getWritableDatabase();
        database.execSQL(collectionTableHelper.CREATE_TABLE);
    }

    public void open(String table_name) throws SQLException {
        collectionTableHelper = new CollectionTableHelper(context, table_name);
        database = collectionTableHelper.getWritableDatabase();
        database.execSQL(collectionTableHelper.CREATE_TABLE);
    }

    public void close() {
        if (collectionTableHelper != null) {
            collectionTableHelper.close();
        }
    }

    public long insert(String name, byte[] image, String description) {
        database.beginTransactionNonExclusive();
        try {
            database.execSQL(DeckTableHelper.createTableQuery(name));
            database.execSQL(SuperMemoTableHelper.createTableQuery(name));
            database.setTransactionSuccessful();
            database.endTransaction();
        }
        catch (SQLiteException exception) {
            database.endTransaction();
            return -1;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(CollectionTableHelper.NAME, name);
        contentValues.put(CollectionTableHelper.IMAGE, image);
        contentValues.put(CollectionTableHelper.DESCRIPTION, description);
        return database.insert(collectionTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { CollectionTableHelper.ID, CollectionTableHelper.NAME, CollectionTableHelper.IMAGE, CollectionTableHelper.DESCRIPTION };
        Cursor cursor = database.query(collectionTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String oldName, String name, byte[] image, String description) {
        if (!oldName.toUpperCase().replace(" ", "_").equals(name.toUpperCase().replace(" ", "_"))) {
            database.beginTransactionNonExclusive();
            try {
                database.execSQL("ALTER TABLE " + "\"" + DeckTableHelper.TABLE_NAME_PREFIX + oldName.toUpperCase().replace(" ", "_") + "\"" + " RENAME TO " + "\"" + DeckTableHelper.TABLE_NAME_PREFIX + name.toUpperCase().replace(" ", "_") + "\"");
                database.execSQL("ALTER TABLE " + "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + oldName.toUpperCase().replace(" ", "_") + "\"" + " RENAME TO " + "\"" + SuperMemoTableHelper.TABLE_NAME_PREFIX + name.toUpperCase().replace(" ", "_") + "\"");
                database.setTransactionSuccessful();
                database.endTransaction();
            } catch (SQLiteException exception) {
                database.endTransaction();
                return 0;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(CollectionTableHelper.NAME, name);
        contentValues.put(CollectionTableHelper.IMAGE, image);
        contentValues.put(CollectionTableHelper.DESCRIPTION, description);
        return database.update(collectionTableHelper.TABLE_NAME, contentValues, CollectionTableHelper.ID + " = " + id, null);
    }

    public int deleteRow(long id, String table_name) {
        database.execSQL(DeckTableHelper.deleteTableQuery(table_name));
        database.execSQL(SuperMemoTableHelper.deleteTableQuery(table_name));
        return database.delete(collectionTableHelper.TABLE_NAME, CollectionTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.execSQL("DROP TABLE IF EXISTS " + collectionTableHelper.TABLE_NAME);
    }
}
