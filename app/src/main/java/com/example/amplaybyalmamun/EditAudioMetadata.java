package com.example.amplaybyalmamun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.process.ResetMetaData;
import com.google.android.material.textfield.TextInputEditText;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class EditAudioMetadata extends AppCompatActivity {

    MyAudioFile audioFile = new MyAudioFile();
    TextInputEditText et_name, et_title, et_album, et_artist, et_albumArtist, et_genre, et_tags, et_year, et_date, et_track;
    AppCompatImageButton btnBack, btnReset_title, btnReset_album, btnReset_artist, btnReset_albumArtist, btnReset_genre, btnReset_tags, btnReset_year, btnReset_date, btnReset_track, btnReset_name;
    AppCompatCheckBox cb_rename;
    AppCompatImageView editAlbumArt;
    AppCompatButton btnSave, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_audio_metadata);

        // assign items
        assignItems();

        Intent intent = getIntent();
        int position = intent.getIntExtra(Keys.POSITION, -1);
        if (position == -1) {
            finish();
            return;
        }

        // get audio file
        audioFile = Store.AUDIO_FILES.get(position);
        if (audioFile == null) {
            MyUtils.showProblem(this); finish();}
        // set data at field
        setData();

        // make cb_rename checked
        et_name.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) cb_rename.setChecked(true);
        });


        // setter meta data
        ResetMetaData reset = new ResetMetaData(audioFile);
        reset.setEt_title(et_title);
        reset.setEt_album(et_album);
        reset.setEt_artist(et_artist);
        reset.setEt_albumArtist(et_albumArtist);
        reset.setEt_genre(et_genre);
        reset.setEt_tags(et_tags);
        reset.setEt_year(et_year);
        reset.setEt_date(et_date);
        reset.setEt_track(et_track);
        reset.setEt_name(et_name);
        // title
        btnReset_title.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_title.setText(reset.title());
        });
        // album
        btnReset_album.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_album.setText(reset.album());
        });
        // artist
        btnReset_artist.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_artist.setText(reset.artist());
        });
        // albumArtist
        btnReset_albumArtist.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_albumArtist.setText(reset.albumArtist());
        });
        // genre
        btnReset_genre.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_genre.setText(reset.genre());
        });
        // tags
        btnReset_tags.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_tags.setText(reset.tags());
        });
        // year
        btnReset_year.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_year.setText(reset.year());
        });
        // date
        btnReset_date.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_date.setText(reset.date());
        });
        // track
        btnReset_track.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_track.setText(reset.track());
        });
        // rename
        btnReset_name.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            et_name.setText(reset.name());
            cb_rename.setChecked(true);
        });

        // edit metadata
        btnSave.setOnClickListener(v -> {
            // get inputs
            MyAudioFile bundle = getInputs();
            // set / update metadata
            updateMetadata(audioFile.getPath(), bundle);

            // rename file
            if (cb_rename.isChecked()) {
                String newPath = audioFile.getDirPath() + "/" + Objects.requireNonNull(et_name.getText()) + ".mp3";
                if (!isValidFileName(et_name.getText().toString())) {
                    Toast.makeText(this, "Invalid file name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (MyUtils.rename(new File(audioFile.getPath()), new File(newPath))) {
                    Toast.makeText(this, "Renamed", Toast.LENGTH_SHORT).show();
                    bundle.setPath(newPath);
                }
            }
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            MyUtils.notifyMediaScanner(this, bundle.getPath()); // notify storage
            Store.AUDIO_FILES.get(position).copyTo(bundle); // updates
            finish();
        });

        btnReset.setOnClickListener(v -> {
            et_track.setText(reset.track());
            et_year.setText(reset.year());
            et_date.setText(reset.date());
            et_genre.setText(reset.genre());
            et_tags.setText(reset.tags());
            et_title.setText(reset.title());
            et_album.setText(reset.album());
            et_artist.setText(reset.artist());
            et_albumArtist.setText(reset.albumArtist());
            // et_name.setText(reset.name());
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void assignItems() {
        editAlbumArt = findViewById(R.id.editAlbumArt);
        et_title = findViewById(R.id.et_title);
        et_album = findViewById(R.id.et_album);
        et_artist = findViewById(R.id.et_artist);
        et_albumArtist = findViewById(R.id.et_AlbumArtist);
        et_genre = findViewById(R.id.et_genre);
        et_tags = findViewById(R.id.et_tags);
        et_year = findViewById(R.id.et_year);
        et_track = findViewById(R.id.et_track);
        et_name = findViewById(R.id.et_name);
        et_date = findViewById(R.id.et_date);
        cb_rename = findViewById(R.id.cb_rename);
        btnBack = findViewById(R.id.editBtnBack);
        btnSave = findViewById(R.id.editBtnSave);
        btnReset = findViewById(R.id.editBtnReset);
        // reset buttons
        btnReset_title = findViewById(R.id.ib_btnResetTitle);
        btnReset_album = findViewById(R.id.ib_btnResetAlbum);
        btnReset_artist = findViewById(R.id.ib_btnResetArtist);
        btnReset_albumArtist = findViewById(R.id.ib_btnResetAlbumArtist);
        btnReset_genre = findViewById(R.id.ib_btnResetGenre);
        btnReset_tags = findViewById(R.id.ib_btnResetTags);
        btnReset_year = findViewById(R.id.ib_btnResetYear);
        btnReset_date = findViewById(R.id.ib_btnResetDate);
        btnReset_track = findViewById(R.id.ib_btnResetTrack);
        btnReset_name = findViewById(R.id.ib_btnResetName);
    }
    private MyAudioFile getInputs() {
        MyAudioFile newDataBundle = new MyAudioFile();

        newDataBundle.setTitle(Objects.requireNonNull(et_title.getText()).toString());
        newDataBundle.setAlbum(Objects.requireNonNull(et_album.getText()).toString());
        newDataBundle.setArtists(Objects.requireNonNull(et_artist.getText()).toString());
        newDataBundle.setAlbumArtist(Objects.requireNonNull(et_albumArtist.getText()).toString());
        newDataBundle.setGenre(Objects.requireNonNull(et_genre.getText()).toString());
        newDataBundle.setAmTags(Objects.requireNonNull(et_tags.getText()).toString());
        newDataBundle.setYear(Objects.requireNonNull(et_year.getText()).toString());
        newDataBundle.setDate(Objects.requireNonNull(et_date.getText()).toString());
        newDataBundle.setTrack(Objects.requireNonNull(et_track.getText()).toString());

        return  newDataBundle;
    }
    private void setData() {
        Bitmap bm = audioFile.getAlbumArt();
        if (bm != null) editAlbumArt.setImageBitmap(bm);
        et_title.setText(audioFile.getTitle());
        et_album.setText(audioFile.getAlbum());
        et_artist.setText(audioFile.getArtists());
        et_albumArtist.setText(audioFile.getAlbumArtist());
        et_genre.setText(audioFile.getGenre());
        et_tags.setText(audioFile.getAmTags());
        et_year.setText(audioFile.getYear());
        et_date.setText(audioFile.getDate());
        et_track.setText(audioFile.getTrack());
        et_name.setText(audioFile.getName());
    }

    private void updateMetadata(String path, MyAudioFile bundle) {
        try {
            String title, album, artist, albumArtist, genre, tags, year, date, track;
            title           = bundle.getTitle();
            album           = bundle.getAlbum();
            artist          = bundle.getArtists();
            albumArtist     = bundle.getAlbumArtist();
            genre           = bundle.getGenre();
            tags            = bundle.getAmTags();
            year            = bundle.getYear();
            date            = bundle.getDate();
            track           = bundle.getTrack();

            AudioFile f = AudioFileIO.read(new File(path));
            Tag tag = f.getTag();

            // set metadata
            if (!title.isEmpty()) tag.setField(FieldKey.TITLE, title.trim());
            if (!album.isEmpty()) tag.setField(FieldKey.ALBUM, album.trim());
            if (!artist.isEmpty()) tag.setField(FieldKey.ARTIST, artist.trim());
            if (!albumArtist.isEmpty()) tag.setField(FieldKey.ALBUM_ARTIST, albumArtist.trim());
            if (!genre.isEmpty()) tag.setField(FieldKey.GENRE, genre.trim());
            if (!tags.isEmpty()) tag.setField(FieldKey.TAGS, tags.trim());
            if (!year.isEmpty()) tag.setField(FieldKey.YEAR, year.trim());
            if (!date.isEmpty()) tag.setField(FieldKey.ISRC, date.trim());
            if (!track.isEmpty()) tag.setField(FieldKey.TRACK, track.trim());

            f.commit();
        } catch (CannotWriteException | CannotReadException | InvalidAudioFrameException |
                 TagException | ReadOnlyFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidFileName(String fileName) {
        char[] invalidFileNameChars = {'/', '\\', ':', '*', '?', '"', '<', '>', '|'};
        for (char c : fileName.toCharArray())
            if (isPresent(invalidFileNameChars, c)) return false;
        return true;
    }
    private boolean isPresent(char[] arr, char c) {
        for (char crr : arr)
            if (crr == c) return true;
        return false;
    }
}
