package com.technophile.nuro.help;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HelpTableManager {

    private HelpTableHelper helpTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public HelpTableManager(Context c) {
        context = c;
    }

    public void open(String table_name) throws SQLException {
        helpTableHelper = new HelpTableHelper(context, table_name);
        database = helpTableHelper.getWritableDatabase();
        database.execSQL(helpTableHelper.CREATE_TABLE);
    }

    public void close() {
        if (helpTableHelper != null) {
            helpTableHelper.close();
        }
    }

    public long insert(String name, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HelpTableHelper.NAME, name);
        contentValues.put(HelpTableHelper.IMAGE, image);
        return database.insert(helpTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { HelpTableHelper.ID, HelpTableHelper.NAME, HelpTableHelper.IMAGE };
        Cursor cursor = database.query(helpTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String name, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HelpTableHelper.NAME, name);
        contentValues.put(HelpTableHelper.IMAGE, image);
        return database.update(helpTableHelper.TABLE_NAME, contentValues, HelpTableHelper.ID + " = " + id, null);
    }

    public int deleteRow(long id) {
        return database.delete(helpTableHelper.TABLE_NAME, HelpTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.execSQL("DROP TABLE IF EXISTS " + helpTableHelper.TABLE_NAME);
    }
}
