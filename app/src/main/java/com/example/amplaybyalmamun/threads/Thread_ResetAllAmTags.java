package com.example.amplaybyalmamun.threads;

import android.app.Dialog;
import android.content.Context;

import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.Store;

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

public class Thread_ResetAllAmTags extends Thread {
    Context context;
    Dialog dialog;

    public Thread_ResetAllAmTags(Context context, Dialog dialog) {
        super();
        this.context = context;
        this.dialog = dialog;
    }

    @Override
    public void run() {
        if (Store.AUDIO_FILES == null) return;
        for (MyAudioFile file : Store.AUDIO_FILES) {
            // if file is null | file not exists | main tag already exists in am tags => continue
            if (file == null || !file.exists() || (!file.getMainAmTag().equals("") && file.getAmTags().toLowerCase().contains(file.getMainAmTag().toLowerCase()))) continue;

            try {
                AudioFile f = AudioFileIO.read(new File(file.getPath()));
                Tag tag = f.getTag();

                tag.setField(FieldKey.TAGS, file.getMainAmTag() + ((!file.getAmTags().equals("") ? ", " + file.getAmTags() : "")));
                f.commit();

            } catch (CannotReadException | IOException | TagException | ReadOnlyFileException |
                     InvalidAudioFrameException | CannotWriteException e) {
                throw new RuntimeException(e);
            }
        }

        if (dialog != null) dialog.dismiss();
    }
}
