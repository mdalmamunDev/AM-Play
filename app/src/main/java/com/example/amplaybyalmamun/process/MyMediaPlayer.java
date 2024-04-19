package com.example.amplaybyalmamun.process;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
//import com.example.amplaybyalmamun.threads.Thread_Refresh;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MyMediaPlayer extends MediaPlayer {

    private final Context context;
    List<MyAudioFile> listFiles;
    private Set<View> setBtn_playPause;
    private boolean isSet = false;

    public MyMediaPlayer(Context context, List<MyAudioFile> listFiles, Set<View> setBtn_playPause) {
        super();
        this.context = context;
        this.listFiles = listFiles;
        this.setBtn_playPause = setBtn_playPause;
    }

    // set View[] arrBtnPlayPause;
    public void setPlayPauseBtns(Set<View> arrBtnPlayPause) {
        this.setBtn_playPause = arrBtnPlayPause;
    }
    public void addPlayPauseBtn(View btn) {
        setBtn_playPause.add(btn);
    }



    @Override
    public void start(){
        try {
            super.start();
            changeBtnImgRes(R.drawable.ic_pause_40);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stop(){
        try {
            super.stop();
            changeBtnImgRes(R.drawable.ic_play_40);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void pause(){
        try {
            super.pause();
            changeBtnImgRes(R.drawable.ic_play_40);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        super.reset();
        changeBtnImgRes(R.drawable.ic_play_40);
    }

    public void setPlayer(String path) {
        if (path == null || path.equals("")) return;
        try {
            setDataSource(path);
            prepare();
            isSet = true;

            /* add to history db*/
            DB_Helper dbHelper = new DB_Helper(context);
            // get pre added
            MyAudioFile preAddedFile = dbHelper.getAudioItem(DB_Helper.TABLE_AUDIO_HISTORY, listFiles.get(MyAudioPlayer.position));
            // add present
            dbHelper.addAudioItem(DB_Helper.TABLE_AUDIO_HISTORY, listFiles.get(MyAudioPlayer.position));
            // remove pre added
            if (preAddedFile != null) dbHelper.deleteAudioItem(DB_Helper.TABLE_AUDIO_HISTORY, preAddedFile.getIdDB());
            dbHelper.close();

            // refresh
//            Thread_Refresh refresh = new Thread_Refresh(context);
//            refresh.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // get is set

    public boolean isSet() {
        return isSet;
    }

    // change icon res
    private void changeBtnImgRes(@DrawableRes int res) {
        if (setBtn_playPause != null)
            for (View btn : setBtn_playPause) {
                if (btn == null) continue;
                AppCompatImageButton btnPlayPause = (AppCompatImageButton) btn;
                btnPlayPause.setImageResource(res);
            }
    }
}
