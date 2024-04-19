package com.example.amplaybyalmamun.threads;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

public class Thread_LoadImg extends Thread{
    Context context;
    MyAudioFile file;
    AppCompatImageView imageView;

    public Thread_LoadImg(Context context, MyAudioFile file, AppCompatImageView imageView) {
        this.context = context;
        this.file = file;
        this.imageView = imageView;
    }

    @Override
    public void run() {
        file.prepare(context);
        Bitmap bm = file.getAlbumArt();
        imageView.post(() -> {
            if (bm != null) {
            /*Glide.with(context)
                    .load(bm)
                    .into(imageView);*/
                imageView.setImageBitmap(bm);
            }
        });
    }
}
