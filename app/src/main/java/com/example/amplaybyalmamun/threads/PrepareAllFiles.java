package com.example.amplaybyalmamun.threads;

import android.content.Context;
import android.util.Log;

import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrepareAllFiles{
    Context context;
    List<MyAudioFile> audioFiles;
    public PrepareAllFiles(Context context, List<MyAudioFile> audioFiles) {
        this.context = context;
        this.audioFiles = audioFiles;
    }

    public void prepare() {
        List<TagLoader> ppList = new ArrayList<>();
        for (MyAudioFile file : audioFiles) {
            if(file.isSetPrimary) continue;
            TagLoader p = new TagLoader(context, file);
            p.start();
            ppList.add(p);
        }

        for (TagLoader p : ppList) {
            if (p.isAlive()) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class TagLoader extends Thread {
        Context context;
        MyAudioFile file;

        TagLoader(Context context, MyAudioFile file) {
            this.file = file;
            this.context = context;
        }

        @Override
        public void run() {
            if (file == null || context == null) return;
            setPriority(MAX_PRIORITY);


            try {
                // Create a file object for the audio file you want to read the metadata from
                File f = new File(file.getPath());
                if(!f.exists()) return;

                AudioFile audio = AudioFileIO.read(f);
                file.setAmTags(audio.getTag().getFirst(FieldKey.TAGS));
                file.setYear(audio.getTag().getFirst(FieldKey.YEAR));
                file.setDate(audio.getTag().getFirst(FieldKey.ISRC));
                file.isSetPrimary = true;
            } catch (CannotReadException | TagException | InvalidAudioFrameException |
                     ReadOnlyFileException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
