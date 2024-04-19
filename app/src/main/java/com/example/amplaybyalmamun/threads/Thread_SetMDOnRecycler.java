package com.example.amplaybyalmamun.threads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.amplaybyalmamun.PlayAudio;
import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.ViewHolder;
import com.example.amplaybyalmamun.gadgets.utils.Store;

import java.util.List;

public class Thread_SetMDOnRecycler extends Thread {
    private final Context context;
    private final ViewHolder holder;
    private final List<MyAudioFile> listFiles;
    private final MyAudioFile audioFile;
    private final int position, size;

    public Thread_SetMDOnRecycler(Context context, ViewHolder holder, List<MyAudioFile> listFiles, int position) {
        this.context = context;
        this.holder = holder;
        this.listFiles = listFiles;
        this.audioFile = listFiles.get(position);
        this.position = position;
        this.size = listFiles.size();
    }

    @Override
    public void run() {
        // set data
        audioFile.prepare(context);
        // set img
        final Bitmap albumArtBitmap = audioFile.getAlbumArt();
        final String title = audioFile.getTitle();
        final String album = audioFile.getAlbum();
        final String artist = audioFile.getArtists();
        final String duration = audioFile.getDurationInTime();

        StringBuilder tags = new StringBuilder();
        final List<String> tagList = audioFile.getTagsList();
        if (tagList != null)
            for (String tag : tagList)
                if (tag != null && !tag.equals(""))
                    tags.append("#").append(tag).append(" ");

        // Update UI on the main thread
        holder.itemView.post(() -> {
            try {
                holder.tvTitle.setText(title);
                if (!album.equals("")) holder.tvAlbum.setText(album);
                if (!artist.equals("")) holder.tvArtist.setText(artist);
                holder.tvDuration.setText(duration);
                holder.tvTags.setText(!tags.toString().equals("") ? tags.toString() : "N/A");

                // set playing anim if playing
                if (audioFile.isPlaying()) {
                    // show anim
                    holder.animMusicPlaying.setVisibility(View.VISIBLE);
                    // change text colors
                    holder.tvColorPlaying(context);
                }
                else {
                    holder.animMusicPlaying.setVisibility(View.GONE);
                    // set text colors
                    holder.tvColorNormal(context);
                }

                // on click item
                holder.itemCon.setOnClickListener(v -> {
                    // set audioFiles of PlayAudio
                    Store.playing_queue = this.listFiles;

                    // Launch PlayActivity and pass the audioFile
                    Intent intent = new Intent(context, PlayAudio.class);
                    intent.putExtra(Keys.POSITION, position);
                    intent.putExtra(Keys.TOTAL_AUDIOS, size);
                    context.startActivity(intent);
                });


                // set album art
                if (audioFile.getAlbumArt() != null) {
                    holder.ivAlbumArt.setImageBitmap(albumArtBitmap);
                } else {
                    holder.ivAlbumArt.setImageResource(R.drawable.img_def_album_art);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}