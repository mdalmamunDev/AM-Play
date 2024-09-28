package com.example.amplaybyalmamun.process;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.MediaStore;

import static com.example.amplaybyalmamun.gadgets.enums.Keys.SORT_ASC;
import static com.example.amplaybyalmamun.gadgets.enums.Keys.SORT_DESC;

public class AppSettings {
    private static final String PREF_NAME = "MyAppSettings";
    private static final String KEY_SORT_BY = "setting_sortBy";
    private static final String KEY_SHUFFLE_STATUS = "setting_shuffleStatus";
    private static final String KEY_REPEAT_STATUS = "setting_repeatStatus";
    private static final String KEY_LAST_SELECTED_TAB = "lastSelectedTabIndex";

    public static int REPEAT_ALL = 0;
    public static int REPEAT_ONE = 1;
    public static int REPEAT_ORDER = 2;
    public static String sortOrder = MediaStore.Audio.Media.TITLE + SORT_ASC;
    public static boolean shuffleStatus = false;
    public static int repeatStatus = REPEAT_ALL;
    public static int lastSelectedTabIdx = 0;
    public static com.example.amplaybyalmamun.process.AppSettings AppSettings;

    // Add more keys as needed

    private final SharedPreferences sharedPreferences;

    public AppSettings(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public void prepare(){
        sortOrder = getSortOrder();
        shuffleStatus = getShuffleStatus();
        repeatStatus = getRepeatStatus();
        lastSelectedTabIdx = getLastSelectedTabIdx();
    }


    // sort order
    public void setSortOrder(String value, boolean isAsc) {
        if (isAsc) value += SORT_ASC;
        else value += SORT_DESC;
        sharedPreferences.edit().putString(KEY_SORT_BY, value).apply();
        sortOrder = value;
    }
    private String getSortOrder() {
        return sharedPreferences.getString(KEY_SORT_BY, MediaStore.Audio.Media.TITLE + SORT_ASC);
    }

    // shuffle
    public void setShuffleStatus(boolean value) {
        sharedPreferences.edit().putBoolean(KEY_SHUFFLE_STATUS, value).apply();
        shuffleStatus = value;
    }
    public boolean getShuffleStatus() {
        return sharedPreferences.getBoolean(KEY_SHUFFLE_STATUS, false);
    }

    // repeat
    public void setRepeatStatus(int value) {
        sharedPreferences.edit().putInt(KEY_REPEAT_STATUS, value).apply();
        repeatStatus = value;
    }
    public int getRepeatStatus() {
        return sharedPreferences.getInt(KEY_REPEAT_STATUS, REPEAT_ALL);
    }

    // last selected tab
    public void setLastSelectedTabIdx(int idx) {
        sharedPreferences.edit().putInt(KEY_LAST_SELECTED_TAB, idx).apply();
        lastSelectedTabIdx = idx;
    }
    public int getLastSelectedTabIdx() {
        return sharedPreferences.getInt(KEY_LAST_SELECTED_TAB, 0);
    }

    // Add more setter and getter methods for other settings
}

