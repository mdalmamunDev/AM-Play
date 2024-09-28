package com.example.amplaybyalmamun.gadgets.utils;

import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

import java.util.ArrayList;
import java.util.List;

// runtime store
public class Store {
    public static List<MyAudioFile> AUDIO_FILES = new ArrayList<>();
    public static List<MyAudioFile> playing_queue = new ArrayList<>();
    public static int mainTagLove = 0, mainTagBroken = 1, mainTagHeat = 2;
    public static String[] mainAmTags = {"Love", "Broken", "Heat"};
    public static boolean isThreadRefreshRunning = false;
    public static int maxVisited = -1;
}
