package com.example.amplaybyalmamun.gadgets.models;

import androidx.annotation.NonNull;

import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    List<MyAudioFile> listFiles = new ArrayList<>();
    String title = "";
    boolean isPlayingFromThis;

    public ItemGroup() {

    }
    public ItemGroup(String title) {
        this.title = title;
    }


    // setters

    public void setListFiles(@NonNull List<MyAudioFile> listFiles) {
        this.listFiles = listFiles;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setPlayingFromThis(boolean playingFromThis) {
        isPlayingFromThis = playingFromThis;
    }


    // getters
    public String getTitle() {
        return title;
    }
    public int getCount() {
        return listFiles.size();
    }
    public long getLength() {
        long length = 0;
        if (listFiles == null) return length;

        for (MyAudioFile f : listFiles) {
            length += f.getDuration();
        }

        return length;
    }

    public List<MyAudioFile> getListFiles() {
        return listFiles;
    }

    public boolean isPlayingFromThis() {
        return isPlayingFromThis;
    }
}
