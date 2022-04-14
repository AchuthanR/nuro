package com.capstone.autism_training.help;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HelpCardTableManager {

    private HelpCardTableHelper helpCardTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public HelpCardTableManager(Context c) {
        context = c;
    }

    public void open(String table_name) throws SQLException {
        helpCardTableHelper = new HelpCardTableHelper(context, table_name);
        database = helpCardTableHelper.getWritableDatabase();
        database.execSQL(helpCardTableHelper.CREATE_TABLE);
    }

    public void close() {
        helpCardTableHelper.close();
    }

    public long insert(String name, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HelpCardTableHelper.NAME, name);
        contentValues.put(HelpCardTableHelper.IMAGE, image);
        return database.insert(helpCardTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { HelpCardTableHelper.ID, HelpCardTableHelper.NAME, HelpCardTableHelper.IMAGE };
        Cursor cursor = database.query(helpCardTableHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String name, byte[] image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HelpCardTableHelper.NAME, name);
        contentValues.put(HelpCardTableHelper.IMAGE, image);
        return database.update(helpCardTableHelper.TABLE_NAME, contentValues, HelpCardTableHelper.ID + " = " + id, null);
    }

    public void deleteRow(long id) {
        database.delete(helpCardTableHelper.TABLE_NAME, HelpCardTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.delete(helpCardTableHelper.TABLE_NAME, null, null);
    }
}
