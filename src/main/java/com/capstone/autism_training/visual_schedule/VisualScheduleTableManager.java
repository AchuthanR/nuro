package com.capstone.autism_training.visual_schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class VisualScheduleTableManager {

    private VisualScheduleTableHelper visualScheduleTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public VisualScheduleTableManager(Context c) {
        context = c;
    }

    public void open(String table_name) throws SQLException {
        visualScheduleTableHelper = new VisualScheduleTableHelper(context, table_name);
        database = visualScheduleTableHelper.getWritableDatabase();
        database.execSQL(VisualScheduleTableHelper.CREATE_TABLE);
    }

    public void close() {
        visualScheduleTableHelper.close();
    }

    public long insert(String name, byte[] image, String instruction, long start_time, long duration) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VisualScheduleTableHelper.NAME, name);
        contentValues.put(VisualScheduleTableHelper.IMAGE, image);
        contentValues.put(VisualScheduleTableHelper.INSTRUCTION, instruction);
        contentValues.put(VisualScheduleTableHelper.START_TIME, start_time);
        contentValues.put(VisualScheduleTableHelper.DURATION, duration);
        return database.insert(VisualScheduleTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { VisualScheduleTableHelper.ID, VisualScheduleTableHelper.NAME, VisualScheduleTableHelper.IMAGE, VisualScheduleTableHelper.INSTRUCTION, VisualScheduleTableHelper.START_TIME, VisualScheduleTableHelper.DURATION };
        Cursor cursor = database.query(VisualScheduleTableHelper.TABLE_NAME, columns, null, null, null, null, VisualScheduleTableHelper.START_TIME);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String name, byte[] image, String instruction, long start_time, long duration) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VisualScheduleTableHelper.NAME, name);
        contentValues.put(VisualScheduleTableHelper.IMAGE, image);
        contentValues.put(VisualScheduleTableHelper.INSTRUCTION, instruction);
        contentValues.put(VisualScheduleTableHelper.START_TIME, start_time);
        contentValues.put(VisualScheduleTableHelper.DURATION, duration);
        return database.update(VisualScheduleTableHelper.TABLE_NAME, contentValues, VisualScheduleTableHelper.ID + " = " + id, null);
    }

    public void deleteRow(long id) {
        database.delete(VisualScheduleTableHelper.TABLE_NAME, VisualScheduleTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.delete(VisualScheduleTableHelper.TABLE_NAME, null, null);
    }
}
