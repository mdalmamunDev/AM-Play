package com.example.amplaybyalmamun.threads;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.utils.BlurUtil;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

public class BlurBgLoader extends Thread{
    Context context;
    MyAudioFile file;
    AppCompatImageView blurView;

    public BlurBgLoader(Context context, MyAudioFile file, AppCompatImageView blurView) {
        this.context = context;
        this.file = file;
        this.blurView = blurView;
    }

    @Override
    public void run() {
        Bitmap art = (file == null) ? null : file.getAlbumArt(); // no need to prepare this file, already it
        Bitmap blurredBitmap = (art != null) ? BlurUtil.blur(context, art, 25.0f) : BlurUtil.blur(context, R.drawable.img_def_album_art, 25.0f);
        blurView.post(() -> blurView.setImageBitmap(blurredBitmap));
    }
}
