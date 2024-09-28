package com.example.amplaybyalmamun.gadgets;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout itemCon;
    public AppCompatImageView ivAlbumArt;
    public TextView tvTitle, tvAlbum, tvArtist, tvDuration, tvRank, tvTags;
    public LinearLayoutCompat animMusicPlaying;

    public ViewHolder(View view) {
        super(view);
        itemCon = (ConstraintLayout) view;
        tvTitle = view.findViewById(R.id.tv_title);
        tvArtist = view.findViewById(R.id.playBar_tv_artist);
        tvAlbum = view.findViewById(R.id.playBar_tv_album);
        tvDuration = view.findViewById(R.id.playBar_tv_duration);
        tvRank = view.findViewById(R.id.playBar_tv_rank);
        tvTags = view.findViewById(R.id.playBar_tv_tags);
        ivAlbumArt = view.findViewById(R.id.iv_itemImg);
        animMusicPlaying = view.findViewById(R.id.anim_music_playing);
    }

    public void tvColorNormal(Context context) {
        int color = ContextCompat.getColor(context, R.color.def_text);
        int color_2 = ContextCompat.getColor(context, R.color.def_text_light);
        int color_3 = ContextCompat.getColor(context, R.color.def_text_light_x);
        this.tvTitle.setTextColor(color);
        this.tvAlbum.setTextColor(color_2);
        this.tvArtist.setTextColor(color_2);
        this.tvDuration.setTextColor(color_2);
        this.tvRank.setTextColor(color_3);
        this.tvTags.setTextColor(color_3);
    }

    public void tvColorPlaying(Context context) {
        int color = ContextCompat.getColor(context, R.color.primary);
        this.tvTitle.setTextColor(color);
        this.tvAlbum.setTextColor(color);
        this.tvArtist.setTextColor(color);
        this.tvDuration.setTextColor(color);
        this.tvRank.setTextColor(color);
        this.tvTags.setTextColor(color);
    }
}

