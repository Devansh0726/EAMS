package com.employee_attendance_management.eam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.employee_attendance_management.eam.models.AttendanceRecord;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Attendance.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    private static final String TABLE_NAME = "attendance";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_STATUS = "status"; // 'check-in' or 'check-out'

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_STATUS + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert data
    public long insertAttendance(String name, byte[] image, String date, String time, double latitude, double longitude, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_STATUS, status);
        return db.insert(TABLE_NAME, null, values);
    }

    // Check if user already checked in for the day
    public boolean isCheckedIn(String name, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_NAME + " = ? AND " +
                COLUMN_DATE + " = ? AND " +
                COLUMN_STATUS + " = 'check-in'", new String[]{name, date});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Update status to check-out
    public void checkOut(String name, String date, String time, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_STATUS, "check-out");

        db.update(TABLE_NAME, values, COLUMN_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{name, date});
    }

    // Check if attendance is completed for the day
    public boolean isAttendanceCompleted(String name, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " +
                        COLUMN_NAME + " = ? AND " +
                        COLUMN_DATE + " = ? AND " +
                        COLUMN_STATUS + " = 'check-out'",
                new String[]{name, date});

        boolean completed = cursor.getCount() > 0; // Check if a 'check-out' entry exists
        cursor.close();
        return completed;
    }


    public List<AttendanceRecord> getAllAttendanceRecords() {
        List<AttendanceRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                COLUMN_IMAGE + ", " +
                COLUMN_TIME + " AS checkInTime, " + // Alias for check-in time
                "(SELECT " + COLUMN_TIME + " FROM " + TABLE_NAME + " AS T2 WHERE T2." + COLUMN_NAME + " = T1." + COLUMN_NAME + " AND T2." + COLUMN_DATE + " = T1." + COLUMN_DATE + " AND T2." + COLUMN_STATUS + " = 'check-out') AS checkOutTime, " + // Subquery for check-out time
                COLUMN_NAME + ", " +
                COLUMN_LATITUDE + ", " +
                COLUMN_LONGITUDE +
                " FROM " + TABLE_NAME + " AS T1" +
                " WHERE " + COLUMN_STATUS + " = 'check-in'"; // Filter for check-in records

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                AttendanceRecord record = new AttendanceRecord();
                record.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)));
                record.setCheckInTime(cursor.getString(cursor.getColumnIndexOrThrow("checkInTime")));
                record.setCheckOutTime(cursor.getString(cursor.getColumnIndexOrThrow("checkOutTime")));
                record.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                record.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)));
                record.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }
}

