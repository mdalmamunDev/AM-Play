package com.example.amplaybyalmamun.process;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.MyAudioColumns;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.threads.PrepareAllFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccessAudioFiles {
    Context context;
    public AccessAudioFiles(Context context) {
        this.context = context;
    }

    public void accessAudioFiles() {
        if (context == null) return;
        ContentResolver contentResolver = context.getContentResolver();

        try {
            // delete/update
            Store.AUDIO_FILES = new ArrayList<>();

            Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            String[] projection = new String[]{
                    MyAudioColumns._ID,
                    MyAudioColumns.DATA, // path
                    MyAudioColumns.TITLE,
                    MyAudioColumns.ALBUM,
                    MyAudioColumns.ARTIST,
                    MyAudioColumns.ALBUM_ARTIST,
                    MyAudioColumns.DURATION,
                    MyAudioColumns.SIZE,
                    MyAudioColumns.MIME_TYPE,
            };
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                projection = new String[]{
                        MyAudioColumns._ID,
                        MyAudioColumns.DATA, // path
                        MyAudioColumns.TITLE,
                        MyAudioColumns.ALBUM,
                        MyAudioColumns.ARTIST,
                        MyAudioColumns.ALBUM_ARTIST,
                        MyAudioColumns.GENRE,
                        MyAudioColumns.DURATION,
                        MyAudioColumns.SIZE,
                        MyAudioColumns.MIME_TYPE,
                };
            }

            String order = AppSettings.sortOrder;
            if (order.contains(MyAudioColumns.DATE_RELEASE)) order = MyAudioColumns.DATE_ADDED;
            Cursor cursor = contentResolver.query(audioUri, projection, null, null, order);

            if (cursor == null) {
                // Cursor is null, log an error or handle the situation accordingly
                return;
            }

            int cl_id = cursor.getColumnIndex(MyAudioColumns._ID);
            int cl_path = cursor.getColumnIndex(MyAudioColumns.DATA); // data == path
            int cl_title = cursor.getColumnIndex(MyAudioColumns.TITLE);
            int cl_album = cursor.getColumnIndex(MyAudioColumns.ALBUM);
            int cl_albumArtist = cursor.getColumnIndex(MyAudioColumns.ALBUM_ARTIST);
            int cl_artist = cursor.getColumnIndex(MyAudioColumns.ARTIST);
            int cl_genre = -1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                cl_genre = cursor.getColumnIndex(MyAudioColumns.GENRE);
            }
            int cl_year = cursor.getColumnIndex(MyAudioColumns.YEAR);
            int cl_track = cursor.getColumnIndex(MyAudioColumns.TRACK);
            int cl_duration = cursor.getColumnIndex(MyAudioColumns.DURATION);
            int cl_size = cursor.getColumnIndex(MyAudioColumns.SIZE);
            int cl_mimeType = cursor.getColumnIndex(MyAudioColumns.MIME_TYPE);
            int cl_bitRate = cursor.getColumnIndex(MyAudioColumns.BITRATE);

            try {
                while (cursor.moveToNext()) {
                    MyAudioFile file = new MyAudioFile();

                    // get data
                    file.setPath((cl_path != -1) ? cursor.getString(cl_path) : "");
                    // Check if the file path is valid and the file exists
                    if (!file.exists()) {
                        // Skip this entry as the file is not accessible
                        continue;
                    }
                    file.setIdFile((cl_id > -1) ? cursor.getInt(cl_id) : -1);
                    file.setTitle((cl_title > -1) ? cursor.getString(cl_title) : "");
                    file.setAlbum((cl_album > -1) ? cursor.getString(cl_album) : "");
                    file.setAlbumArtist((cl_albumArtist > -1) ? cursor.getString(cl_albumArtist) : "");
                    file.setArtists((cl_artist > -1) ? cursor.getString(cl_artist) : "");
                    file.setGenre((cl_genre > -1) ? cursor.getString(cl_genre) : "");
                    file.setYear((cl_year != -1) ? cursor.getString(cl_year) : "");
                    file.setTrack((cl_track != -1) ? cursor.getString(cl_track) : "");
                    file.setDuration((cl_duration != -1) ? cursor.getLong(cl_duration) : -1);
                    file.setMimeType((cl_mimeType != -1) ? cursor.getString(cl_mimeType) : "");
                    file.setSize((cl_size != -1) ? cursor.getLong(cl_size) : -1);
                    file.setBitRate((cl_bitRate != -1) ? cursor.getInt(cl_track) : -1);

                    if (file.isAudio()) Store.AUDIO_FILES.add(file);
                    // check
                    //System.out.println("AccessAudioFiles, path: " + file.getPath());

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

//            // resort audios if it ordered by DateRelease
            if (AppSettings.sortOrder.contains(MyAudioColumns.DATE_RELEASE) && Store.AUDIO_FILES != null) {

                PrepareAllFiles prepare = new PrepareAllFiles(context, Store.AUDIO_FILES);
                prepare.prepare();

                Map<String, List<MyAudioFile>> map = new HashMap<>();
                for (MyAudioFile file : Store.AUDIO_FILES) {
                    String date = file.getYear()+"-"+file.getDate();

                    List<MyAudioFile> crr = map.get(date);
                    if (crr == null) crr = new ArrayList<>();
                    crr.add(file);

                    map.put(date, crr);
                }


                Store.AUDIO_FILES = new ArrayList<>();
                Set<String> keySet;
                if (AppSettings.sortOrder.contains(Keys.SORT_DESC)) keySet = MyUtils.sortSetDescending(map.keySet());
                else keySet = MyUtils.sortSet(map.keySet());
                for (String key : keySet) {
                    List<MyAudioFile> list = map.get(key);
                    if (list != null) Store.AUDIO_FILES.addAll(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
