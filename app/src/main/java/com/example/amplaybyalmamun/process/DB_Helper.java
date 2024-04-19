package com.example.amplaybyalmamun.process;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

import java.util.ArrayList;
import java.util.List;

public class DB_Helper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "audio_database";
    public static final int DATABASE_VERSION = 1;

    private static final String CL_ID = "_id"; // CL == COLUMN
    private static final String CL_ID_FILE = "id_file";
    private static final String CL_PATH = "path";
    private static final String CL_DURATION = "duration";
    private static final String CL_SIZE = "size";
    private static final String CL_MIME_TYPE = "mime_type";



    // table audio history
    public static final String TABLE_AUDIO_HISTORY = "audio_history"; // AH == AUDIO HISTORY
    private static final int HISTORY_LIMITS = 100;
    private static final String TABLE_CREATE_AH =
            "CREATE TABLE " + TABLE_AUDIO_HISTORY + " (" +
                    CL_ID + " INTEGER PRIMARY KEY, " +
                    CL_ID_FILE + " INTEGER, " +
                    CL_PATH + " TEXT, " +
                    CL_DURATION + " INTEGER, " +
                    CL_SIZE + " INTEGER, " +
                    CL_MIME_TYPE + " TEXT)";

    // table audio favorites
    public static final String TABLE_FAVORITES = "audio_favorites";
    private static final String TABLE_CREATE_FAV =
            "CREATE TABLE " + TABLE_FAVORITES + " (" +
                    CL_ID + " INTEGER PRIMARY KEY, " +
                    CL_ID_FILE + " INTEGER, " +
                    CL_PATH + " TEXT, " +
                    CL_DURATION + " INTEGER, " +
                    CL_SIZE + " INTEGER, " +
                    CL_MIME_TYPE + " TEXT)";



    Context context;

    // constructor
    public DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_AH);
        db.execSQL(TABLE_CREATE_FAV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIO_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    // Add a new audio item to the database
    public void addAudioItem(String table, MyAudioFile bundle) {
        if (bundle == null || table.isEmpty()) return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CL_ID_FILE,      bundle.getIdFile());
        values.put(CL_PATH,         bundle.getPath());
        values.put(CL_DURATION,     bundle.getDuration());
        values.put(CL_SIZE,         bundle.getSize());
        values.put(CL_MIME_TYPE,    bundle.getMimeType());

        db.insert(table, null, values);
        //db.close();
    }

    // Update an existing audio item in the database
    public void updateAudioItem(String table, int id, MyAudioFile bundle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CL_ID_FILE,      bundle.getIdFile());
        values.put(CL_PATH,         bundle.getPath());
        values.put(CL_DURATION,     bundle.getDuration());
        values.put(CL_SIZE,         bundle.getSize());
        values.put(CL_MIME_TYPE,    bundle.getMimeType());

        db.update(table, values, CL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Retrieve all audio items from the database
    public List<MyAudioFile> getAllAudioItems(String table) {return getAllAudioItems(table, CL_ID + Keys.SORT_DESC);}
    public List<MyAudioFile> getAllAudioItems(String table, String orderBy) {
        List<MyAudioFile> audioItemList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex        = cursor.getColumnIndexOrThrow(CL_ID);
            int idFileColumnIndex    = cursor.getColumnIndexOrThrow(CL_ID_FILE);
            int pathColumnIndex      = cursor.getColumnIndexOrThrow(CL_PATH);
            int durationColumnIndex  = cursor.getColumnIndexOrThrow(CL_DURATION);
            int sizeColumnIndex      = cursor.getColumnIndexOrThrow(CL_SIZE);
            int mimeTypeColumnIndex  = cursor.getColumnIndexOrThrow(CL_MIME_TYPE);

            int count = 0;
            do {
                // delete old history
                if (table.equals(TABLE_AUDIO_HISTORY) && count >= HISTORY_LIMITS) {
                    deleteAudioItem( TABLE_AUDIO_HISTORY, cursor.getInt(idColumnIndex));
                    continue;
                }

                MyAudioFile audioFile = new MyAudioFile();
                audioFile.setIdDB(      cursor.getInt(idColumnIndex));
                audioFile.setIdFile(    cursor.getLong(idFileColumnIndex));
                audioFile.setPath(      cursor.getString(pathColumnIndex));
                audioFile.setDuration(  cursor.getLong(durationColumnIndex));
                audioFile.setSize(      cursor.getLong(sizeColumnIndex));
                audioFile.setMimeType(  cursor.getString(mimeTypeColumnIndex));

                // if not exists then remove from db
                if (!audioFile.exists()) {
                    deleteAudioItem(table, audioFile.getIdDB());
                    continue;
                }

                audioItemList.add(audioFile);
                count++;
            } while (cursor.moveToNext());

            cursor.close();
        }

        return audioItemList;
    }
    public MyAudioFile getAudioItem(String table, MyAudioFile file) {
        if (file == null) return null;

        SQLiteDatabase db = this.getReadableDatabase();
        MyAudioFile audioFile = null;

        String[] projection = {
                CL_ID,
                CL_ID_FILE,
                CL_PATH,
                CL_DURATION,
                CL_SIZE,
                CL_MIME_TYPE
        };

        String selection = CL_PATH + " = ? AND " + CL_SIZE + " = ? AND " + CL_DURATION + " = ?";
        String[] selectionArgs = { file.getPath(), String.valueOf(file.getSize()), String.valueOf(file.getDuration()) };

        Cursor cursor = db.query(table, projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndexOrThrow(CL_ID);
            int idFileColumnIndex = cursor.getColumnIndexOrThrow(CL_ID_FILE);
            int pathColumnIndex = cursor.getColumnIndexOrThrow(CL_PATH);
            int durationColumnIndex = cursor.getColumnIndexOrThrow(CL_DURATION);
            int sizeColumnIndex = cursor.getColumnIndexOrThrow(CL_SIZE);
            int mimeTypeColumnIndex = cursor.getColumnIndexOrThrow(CL_MIME_TYPE);

            audioFile = new MyAudioFile();
            audioFile.setIdDB(cursor.getInt(idColumnIndex));
            audioFile.setIdFile(cursor.getLong(idFileColumnIndex));
            audioFile.setPath(cursor.getString(pathColumnIndex));
            audioFile.setDuration(cursor.getLong(durationColumnIndex));
            audioFile.setSize(cursor.getLong(sizeColumnIndex));
            audioFile.setMimeType(cursor.getString(mimeTypeColumnIndex));

            // Populate other attributes as needed, perhaps using the prepare() method

            cursor.close();
        }

        return audioFile;
    }
    public MyAudioFile getLastAddedAudioItem(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, CL_ID + " DESC", "1");

        MyAudioFile audioFile = null;

        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndexOrThrow(CL_ID);
            int idFileColumnIndex    = cursor.getColumnIndexOrThrow(CL_ID_FILE);
            int pathColumnIndex = cursor.getColumnIndexOrThrow(CL_PATH);
            int durationColumnIndex = cursor.getColumnIndexOrThrow(CL_DURATION);
            int sizeColumnIndex = cursor.getColumnIndexOrThrow(CL_SIZE);
            int mimeTypeColumnIndex = cursor.getColumnIndexOrThrow(CL_MIME_TYPE);

            audioFile = new MyAudioFile();
            audioFile.setIdDB(      cursor.getInt(idColumnIndex));
            audioFile.setIdFile(    cursor.getLong(idFileColumnIndex));
            audioFile.setIdFile(    cursor.getLong(idColumnIndex));
            audioFile.setPath(  cursor.getString(pathColumnIndex));
            audioFile.setDuration(  cursor.getLong(durationColumnIndex));
            audioFile.setSize(      cursor.getLong(sizeColumnIndex));
            audioFile.setMimeType(  cursor.getString(mimeTypeColumnIndex));

            // Populate other attributes as needed, perhaps using the prepare() method

            cursor.close();
        }

        return audioFile;
    }



    // Delete an audio item from the database
    public void deleteAudioItem( String table, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, CL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}

