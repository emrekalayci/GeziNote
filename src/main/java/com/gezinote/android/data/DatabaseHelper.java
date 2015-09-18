package com.gezinote.android.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gezinote.android.model.Note;
import com.gezinote.android.model.Trip;
import com.gezinote.android.model.TripSpinner;
import com.gezinote.android.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Emre on 20.4.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "gezinote_db";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    private static final String USER_ID = "id";
    private static final String USER_GY_ID = "gy_id";
    private static final String USER_USERNAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_FULLNAME = "fullname";
    private static final String USER_CREATED_AT = "created_at";

    // TABLE TRIP
    private static final String TABLE_TRIPS = "trips";
    private static final String TRIP_ID = "id";
    private static final String TRIP_TITLE = "title";
    private static final String TRIP_START_DATE = "start_date";
    private static final String TRIP_END_DATE = "end_date";
    private static final String TRIP_COLOR = "color";
    // TABLE NOTES
    private static final String TABLE_NOTES = "notes";
    private static final String NOTE_ID = "id";
    private static final String NOTE_TRIP_ID = "trip_id";
    private static final String NOTE_CONTENT = "note_content";
    private static final String NOTE_TIME = "note_date";
    private static final String NOTE_CREATED_AT = "note_created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String createTableLogin = "CREATE TABLE " + TABLE_LOGIN + "("
            + USER_ID + " INTEGER PRIMARY KEY,"
            + USER_GY_ID + " INTEGER,"
            + USER_EMAIL + " TEXT UNIQUE,"
            + USER_USERNAME + " TEXT,"
            + USER_FULLNAME + " TEXT,"
            + USER_CREATED_AT + " TEXT" + ")";

    private static final String createTableTrip = "CREATE TABLE " + TABLE_TRIPS + "("
            + TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TRIP_TITLE + " TEXT,"
            + TRIP_START_DATE + " INTEGER,"
            + TRIP_END_DATE + " INTEGER,"
            + TRIP_COLOR + " INTEGER" + ")";

    private static final String createTableNote = "CREATE TABLE " + TABLE_NOTES + "("
            + NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NOTE_TRIP_ID + " INTEGER,"
            + NOTE_CONTENT + " TEXT,"
            + NOTE_TIME + " INTEGER,"
            + NOTE_CREATED_AT + " INTEGER" + ")";

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createTableLogin);
        db.execSQL(createTableTrip);
        db.execSQL(createTableNote);
    }

    public void insertUser(int gy_id, String email, String username, String fullname, String created_at)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_GY_ID, gy_id);
        values.put(USER_EMAIL, email); // Email
        values.put(USER_USERNAME, username); // Name
        values.put(USER_FULLNAME, fullname); // Email
        values.put(USER_CREATED_AT, created_at); // Created At
        // Inserting Row
        long table_id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    public String getJsonNotes(int tripId)
    {
        List<Note> noteList = new ArrayList<Note>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NOTES + " WHERE " + NOTE_TRIP_ID + " = " + tripId + " ORDER BY " + TRIP_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTripId(cursor.getInt(1));
            note.setContent(cursor.getString(2));
            // Java returns milliseconds since Jan 1, 1970. Unixtime is seconds since Jan 1, 1970.
            // A divide by 1000 gets you to Unix epoch. More info: http://stackoverflow.com/a/732043/2280416
            note.setTime(cursor.getLong(3)/1000L);
            note.setCreatedAt(cursor.getLong(4)/1000L);
            noteList.add(note);
        }

        cursor.close();
        db.close();

        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(noteList);
    }

    public User getUser()
    {
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // Watch out: No getInt(0) since we do not need for local user id
            user.setGyId(cursor.getInt(1));
            user.setEmail(cursor.getString(2));
            user.setUsername(cursor.getString(3));
            user.setFullname(cursor.getString(4));
            user.setCreated_at(cursor.getString(5));
        }
        cursor.close();
        db.close();

        return user;
    }

    /*
     * Getting user login status return true if rows are there in table
     */
    public int getRowCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }

    public void insertTrip(String title, long startDate, long endDate, int colorId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRIP_TITLE, title);
        values.put(TRIP_START_DATE, startDate);
        values.put(TRIP_END_DATE, endDate);
        values.put(TRIP_COLOR, colorId);

        db.insert(TABLE_TRIPS, null, values);
        db.close();
    }

    public void updateTrip(int id, String title, long startDate, long endDate, int colorId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRIP_TITLE, title);
        values.put(TRIP_START_DATE, startDate);
        values.put(TRIP_END_DATE, endDate);
        values.put(TRIP_COLOR, colorId);
        // update row
        db.update(TABLE_TRIPS, values, TRIP_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Trip getTrip(int id)
    {
        Trip trip = new Trip();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_TRIPS + " WHERE id = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            trip.setId(cursor.getInt(0));
            trip.setTitle(cursor.getString(1));
            trip.setStartDate(cursor.getLong(2));
            trip.setEndDate(cursor.getLong(3));
            trip.setColorId(cursor.getInt(4));
        }
        cursor.close();
        db.close();

        return trip;
    }

    public boolean removeTrip(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRow =  db.delete(TABLE_TRIPS, TRIP_ID + "=" + id, null);
        db.close();

        return affectedRow > 0;
    }

    public List<Trip> getTrips()
    {
        List<Trip> tripList = new ArrayList<Trip>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_TRIPS + " ORDER BY " + TRIP_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            Trip trip = new Trip();
            trip.setId(cursor.getInt(0));
            trip.setTitle(cursor.getString(1));
            trip.setStartDate(cursor.getLong(2));
            trip.setEndDate(cursor.getLong(3));
            trip.setColorId(cursor.getInt(4));
            tripList.add(trip);
        }

        cursor.close();
        db.close();

        return tripList;
    }

    public List<TripSpinner> getTripSpinner()
    {
        List<TripSpinner> tripSpinnerList = new ArrayList<TripSpinner>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT " + TRIP_ID + ", " + TRIP_TITLE + " FROM " + TABLE_TRIPS + " ORDER BY " + TRIP_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                tripSpinnerList.add(new TripSpinner(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tripSpinnerList;
    }


    public int countNote(int tripId)
    {
        int num =0;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NOTES + " WHERE " + NOTE_TRIP_ID + " = " + tripId;
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
             num = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return num;
    }

    public void deleteNotes()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "DELETE FROM " + TABLE_NOTES;
        db.execSQL("delete from "+ TABLE_NOTES);

        db.close();
    }

    public void dropTableNotes()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE "+ TABLE_NOTES);
        db.close();
    }

    public void dropTableTrips()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE "+ TABLE_TRIPS);
        db.close();
    }

    public void dropTableLogin()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE "+ TABLE_LOGIN);
        db.close();
    }


    public List<Note> getNotes(int tripId)
    {
        List<Note> noteList = new ArrayList<Note>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NOTES + " WHERE " + NOTE_TRIP_ID + " = " + tripId + " ORDER BY " + NOTE_TIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTripId(cursor.getInt(1));
            note.setContent(cursor.getString(2));
            note.setTime(cursor.getLong(3));
            note.setCreatedAt(cursor.getLong(4));
            noteList.add(note);
        }

        cursor.close();
        db.close();

        return noteList;
    }

    public void insertNote(int tripId, String content, long time)
    {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_TRIP_ID, tripId);
        values.put(NOTE_CONTENT, content);
        values.put(NOTE_TIME, time);
        values.put(NOTE_CREATED_AT, cal.getTime().getTime());

        db.insert(TABLE_NOTES, null, values);
        db.close();
    }

    public boolean removeNote(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int affectedRow =  db.delete(TABLE_NOTES, NOTE_ID + "=" + id, null);
        db.close();

        return affectedRow > 0;
    }

    public Note getNote(int id)
    {
        Note note = new Note();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NOTES + " WHERE id = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            note.setId(cursor.getInt(0));
            note.setTripId(cursor.getInt(1));
            note.setContent(cursor.getString(2));
            note.setTime(cursor.getLong(3));
            note.setCreatedAt(cursor.getLong(4));
        }
        cursor.close();
        db.close();

        return note;
    }

    public void updateNote(int id, int tripId, String content, long time)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_TRIP_ID, tripId);
        values.put(NOTE_CONTENT, content);
        values.put(NOTE_TIME, time);
        // update row
        db.update(TABLE_NOTES, values, NOTE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(createTableTrip);
        db.execSQL(createTableNote);
    }
}
