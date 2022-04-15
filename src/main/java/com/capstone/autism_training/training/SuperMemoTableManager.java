package com.capstone.autism_training.training;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    public long insert(int repetitions, int interval, double easiness) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SuperMemoTableHelper.REPETITIONS, repetitions);
        contentValues.put(SuperMemoTableHelper.INTERVAL, interval);
        contentValues.put(SuperMemoTableHelper.EASINESS, easiness);
        return database.insert(superMemoTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { SuperMemoTableHelper.ID, SuperMemoTableHelper.REPETITIONS, SuperMemoTableHelper.INTERVAL, SuperMemoTableHelper.EASINESS };
        Cursor cursor = database.query(superMemoTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, int repetitions, int interval, double easiness) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SuperMemoTableHelper.REPETITIONS, repetitions);
        contentValues.put(SuperMemoTableHelper.INTERVAL, interval);
        contentValues.put(SuperMemoTableHelper.EASINESS, easiness);
        return database.update(superMemoTableHelper.TABLE_NAME, contentValues, SuperMemoTableHelper.ID + " = " + id, null);
    }

    public void deleteRow(long id) {
        database.delete(superMemoTableHelper.TABLE_NAME, SuperMemoTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.execSQL("DROP TABLE IF EXISTS " + superMemoTableHelper.TABLE_NAME);
    }
}
