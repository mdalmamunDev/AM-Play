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
    private static final int DATABASE_VERSION = 1;

    private static final String CL_ID = "_id";
    private static final String CL_ID_FILE = "id_file";
    private static final String CL_PATH = "path";
    private static final String CL_DURATION = "duration";
    private static final String CL_SIZE = "size";
    private static final String CL_MIME_TYPE = "mime_type";
    private static final String CL_PLAYLIST = "playlist";

    public static final String TABLE_AUDIO_HISTORY = "audio_history";
    public static final String TABLE_AUDIO_FAVORITES = "audio_favorites";
    public static final String TABLE_AUDIO_PLAYLISTS = "audio_playlists";

    private static final String TABLE_CREATE = "CREATE TABLE %s (" +
            CL_ID + " INTEGER PRIMARY KEY, " +
            CL_ID_FILE + " INTEGER, " +
            CL_PATH + " TEXT, " +
            CL_DURATION + " INTEGER, " +
            CL_SIZE + " INTEGER, " +
            CL_MIME_TYPE + " TEXT%s)";

    public DB_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(TABLE_CREATE, TABLE_AUDIO_HISTORY, ""));
        db.execSQL(String.format(TABLE_CREATE, TABLE_AUDIO_FAVORITES, ""));
        db.execSQL(String.format(TABLE_CREATE, TABLE_AUDIO_PLAYLISTS, ", " + CL_PLAYLIST + " TEXT"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIO_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIO_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIO_PLAYLISTS);
        onCreate(db);
    }

    public void addAudioItem(String table, MyAudioFile bundle) {
        addOrUpdateAudioItem(table, bundle, null);
    }
    public void addOrUpdateAudioItem(String table, MyAudioFile bundle, Integer id) {
        if (bundle == null || table.isEmpty()) return;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = getContentValues(bundle, table);
            if (id == null) {
                db.insert(table, null, values);
            } else {
                db.update(table, values, CL_ID + " = ?", new String[]{String.valueOf(id)});
            }
        }
    }

    public MyAudioFile getAudioItem(String table, MyAudioFile file) {
        if (file == null) return null;
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(table, null, CL_PATH + " = ? AND " + CL_SIZE + " = ? AND " + CL_DURATION + " = ?",
                     new String[]{file.getPath(), String.valueOf(file.getSize()), String.valueOf(file.getDuration())},
                     null, null, null)) {
            return cursor.moveToFirst() ? extractAudioFile(cursor, table) : null;
        }
    }

    public List<MyAudioFile> getAllAudioItems(String table) {
        List<MyAudioFile> audioItemList = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(table, null, null, null, null, null, CL_ID + Keys.SORT_DESC)) {
            while (cursor.moveToNext()) {
                audioItemList.add(extractAudioFile(cursor, table));
            }
        }
        return audioItemList;
    }

    public MyAudioFile getLastAddedAudioItem(String table) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(table, null, null, null, null, null, CL_ID + " DESC", "1")) {
            return cursor.moveToFirst() ? extractAudioFile(cursor, table) : null;
        }
    }

    public void deleteAudioItem(String table, int id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(table, CL_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    public List<String> getAudioPlaylists() {
        List<String> playlists = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(true, TABLE_AUDIO_PLAYLISTS, new String[]{CL_PLAYLIST},
                     null, null, null, null, CL_PLAYLIST + " ASC", null)) {
            while (cursor.moveToNext()) {
                playlists.add(cursor.getString(cursor.getColumnIndexOrThrow(CL_PLAYLIST)));
            }
        }
        return playlists;
    }


    private ContentValues getContentValues(MyAudioFile bundle, String table) {
        ContentValues values = new ContentValues();
        values.put(CL_ID_FILE, bundle.getIdFile());
        values.put(CL_PATH, bundle.getPath());
        values.put(CL_DURATION, bundle.getDuration());
        values.put(CL_SIZE, bundle.getSize());
        values.put(CL_MIME_TYPE, bundle.getMimeType());
        if (TABLE_AUDIO_PLAYLISTS.equals(table)) {
            values.put(CL_PLAYLIST, bundle.getPlaylist());
        }
        return values;
    }

    private MyAudioFile extractAudioFile(Cursor cursor, String table) {
        MyAudioFile audioFile = new MyAudioFile();
        audioFile.setIdDB(cursor.getInt(cursor.getColumnIndexOrThrow(CL_ID)));
        audioFile.setIdFile(cursor.getLong(cursor.getColumnIndexOrThrow(CL_ID_FILE)));
        audioFile.setPath(cursor.getString(cursor.getColumnIndexOrThrow(CL_PATH)));
        audioFile.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(CL_DURATION)));
        audioFile.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(CL_SIZE)));
        audioFile.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(CL_MIME_TYPE)));
        if (TABLE_AUDIO_PLAYLISTS.equals(table)) {
            audioFile.setPlaylist(cursor.getString(cursor.getColumnIndexOrThrow(CL_PLAYLIST)));
        }
        return audioFile;
    }

    @Override
    public synchronized void close() {
        //
    }
}
