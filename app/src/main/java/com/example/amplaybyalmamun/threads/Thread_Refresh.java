package com.example.amplaybyalmamun.threads;

import android.content.Context;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.amplaybyalmamun.MainActivity;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.process.AccessAudioFiles;
import com.example.amplaybyalmamun.process.AppSettings;
import com.example.amplaybyalmamun.process.DB_Helper;

public class Thread_Refresh extends Thread{
    Context context = null;
    private boolean accessAudios = false, recyclerAnim = false;
    SwipeRefreshLayout swipeRefreshLayout = null;
    DB_Helper dbHelper;
    public Thread_Refresh(Context context) {
        if (context == null) return;
        this.context = context;
        dbHelper = new DB_Helper(context);
    }
    public Thread_Refresh(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        if (context == null || swipeRefreshLayout == null) return;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        dbHelper = new DB_Helper(context);
    }
    @Override
    public void run() {
        if (!Store.isThreadRefreshRunning) {
            Store.isThreadRefreshRunning = true;

            if (recyclerAnim) Store.maxVisited = -1;

            // access audios
            if (accessAudios) new AccessAudioFiles(context).accessAudioFiles();

            MainActivity mainActivity = MainActivity.getInstance();
            // reset tab layout
            if (mainActivity != null)
                mainActivity.runOnUiThread(() -> {
                    int idx = AppSettings.lastSelectedTabIdx;
                    mainActivity.viewPager.setAdapter(mainActivity.adapter);
                    mainActivity.tabLayout.setupWithViewPager(mainActivity.viewPager);
                    mainActivity.viewPager.setCurrentItem(idx);
                    //if (sf != null) sf.setTotalSongsCount();
                });

            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

            dbHelper.close();
            Store.isThreadRefreshRunning = false;
        }
    }

    public void setRecyclerAnim(boolean val) {
        recyclerAnim = val;
    }
    public void setAccessAudios(boolean access) {
        accessAudios = access;
    }
}
