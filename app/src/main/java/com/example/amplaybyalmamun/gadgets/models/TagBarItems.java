package com.example.amplaybyalmamun.gadgets.models;

import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.adaptes.RecyclerAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class TagBarItems {
    private Context context;
    private List<MyAudioFile> audioFiles;
    private List<String> clickedTags;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutCompat tagsCon;
    private LinearLayoutCompat tagsCon_outer;
    private ShimmerFrameLayout tagsCon_outer_placeHolder;
    private TextView tv_totalSongCount;

    public TagBarItems(Context context) {
        this.context = context;
    }


    // setters
    public TagBarItems setContext(Context context) {
        this.context = context;
        return this;
    }

    public TagBarItems setAudioFiles(List<MyAudioFile> audioFiles) {
        this.audioFiles = audioFiles;
        return this;
    }

    public TagBarItems setClickedTags(List<String> clickedTags) {
        this.clickedTags = clickedTags;
        return this;
    }

    public TagBarItems setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    public TagBarItems setRecyclerAdapter(RecyclerAdapter recyclerAdapter) {
        this.recyclerAdapter = recyclerAdapter;
        return this;
    }

    public TagBarItems setTagsCon(LinearLayoutCompat tagsCon) {
        this.tagsCon = tagsCon;
        return this;
    }

    public TagBarItems setTagsCon_outer(LinearLayoutCompat tagsCon_outer) {
        this.tagsCon_outer = tagsCon_outer;
        return this;
    }

    public TagBarItems setTagsCon_outer_placeHolder(ShimmerFrameLayout tagsCon_outer_placeHolder) {
        this.tagsCon_outer_placeHolder = tagsCon_outer_placeHolder;
        return this;
    }

    public TagBarItems setTv_totalSongCount(TextView tv_totalSongCount) {
        this.tv_totalSongCount = tv_totalSongCount;
        return this;
    }


    // getters

    public Context getContext() {
        return context;
    }

    public List<MyAudioFile> getAudioFiles() {
        return audioFiles;
    }

    public List<String> getClickedTags() {
        return clickedTags;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerAdapter getRecyclerAdapter() {
        return recyclerAdapter;
    }

    public LinearLayoutCompat getTagsCon() {
        return tagsCon;
    }

    public LinearLayoutCompat getTagsCon_outer() {
        return tagsCon_outer;
    }

    public ShimmerFrameLayout getTagsCon_outer_placeHolder() {
        return tagsCon_outer_placeHolder;
    }

    public TextView getTv_totalSongCount() {
        return tv_totalSongCount;
    }
}
