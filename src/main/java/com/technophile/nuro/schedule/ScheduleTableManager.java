package com.technophile.nuro.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ScheduleTableManager {

    private ScheduleTableHelper scheduleTableHelper;

    private final Context context;

    private SQLiteDatabase database;

    public ScheduleTableManager(Context c) {
        context = c;
    }

    public void open(String table_name) throws SQLException {
        scheduleTableHelper = new ScheduleTableHelper(context, table_name);
        database = scheduleTableHelper.getWritableDatabase();
        database.execSQL(scheduleTableHelper.CREATE_TABLE);
    }

    public void close() {
        if (scheduleTableHelper != null) {
            scheduleTableHelper.close();
        }
    }

    public long insert(String name, byte[] image, String instruction, long start_time, long duration) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleTableHelper.NAME, name);
        contentValues.put(ScheduleTableHelper.IMAGE, image);
        contentValues.put(ScheduleTableHelper.INSTRUCTION, instruction);
        contentValues.put(ScheduleTableHelper.START_TIME, start_time);
        contentValues.put(ScheduleTableHelper.DURATION, duration);
        contentValues.put(ScheduleTableHelper.COMPLETED, false);
        contentValues.put(ScheduleTableHelper.CURRENT_END_TIME, -1);
        return database.insert(scheduleTableHelper.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch() {
        String[] columns = new String[] { ScheduleTableHelper.ID, ScheduleTableHelper.NAME, ScheduleTableHelper.IMAGE, ScheduleTableHelper.INSTRUCTION, ScheduleTableHelper.START_TIME, ScheduleTableHelper.DURATION, ScheduleTableHelper.COMPLETED, ScheduleTableHelper.CURRENT_END_TIME };
        Cursor cursor = database.query(scheduleTableHelper.TABLE_NAME, columns, null, null, null, null, ScheduleTableHelper.START_TIME);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String name, byte[] image, String instruction, long start_time, long duration) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleTableHelper.NAME, name);
        contentValues.put(ScheduleTableHelper.IMAGE, image);
        contentValues.put(ScheduleTableHelper.INSTRUCTION, instruction);
        contentValues.put(ScheduleTableHelper.START_TIME, start_time);
        contentValues.put(ScheduleTableHelper.DURATION, duration);
        return database.update(scheduleTableHelper.TABLE_NAME, contentValues, ScheduleTableHelper.ID + " = " + id, null);
    }

    public int update(long id, boolean completed) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleTableHelper.COMPLETED, completed);
        return database.update(scheduleTableHelper.TABLE_NAME, contentValues, ScheduleTableHelper.ID + " = " + id, null);
    }

    public int update(long id, long current_end_time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleTableHelper.CURRENT_END_TIME, current_end_time);
        return database.update(scheduleTableHelper.TABLE_NAME, contentValues, ScheduleTableHelper.ID + " = " + id, null);
    }

    public int markAllAsPending() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ScheduleTableHelper.COMPLETED, false);
        return database.update(scheduleTableHelper.TABLE_NAME, contentValues, null, null);
    }

    public int deleteRow(long id) {
        return database.delete(scheduleTableHelper.TABLE_NAME, ScheduleTableHelper.ID + "=" + id, null);
    }

    public void deleteTable() {
        database.execSQL("DROP TABLE IF EXISTS " + scheduleTableHelper.TABLE_NAME);
    }
}
