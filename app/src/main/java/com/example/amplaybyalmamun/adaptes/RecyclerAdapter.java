package com.example.amplaybyalmamun.adaptes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.ViewHolder;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.threads.Thread_SetMDOnRecycler;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private  List<MyAudioFile> listFiles;
    private final Context context; // Added context variable to store the activity context.

    public RecyclerAdapter(Context context, List<MyAudioFile> listFiles) {
        this.context = context;
        this.listFiles = listFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // set data
        MyAudioFile file = listFiles.get(position);
        holder.ivAlbumArt.setImageResource(R.drawable.img_def_album_art);
        holder.tvTitle.setText(file.getTitle());
        holder.tvAlbum.setText(!file.getAlbum().equals("") ? file.getAlbum() : "<empty>");
        holder.tvArtist.setText(!file.getArtists().equals("") ? file.getArtists() : "<empty>");
        holder.tvDuration.setText(file.getDurationInTime());

        // set color
        holder.tvColorNormal(context);

        // reset data and onclick listener using thread
        new Thread_SetMDOnRecycler(context, holder, listFiles, position).start();
        // Apply animation to the view holder here
        if (position > Store.maxVisited) {
            MyUtils.loadAnimation(holder.itemView, R.anim.anim_recycler_item);
            Store.maxVisited = position;
        }
    }

    @Override
    public int getItemCount() {
        return listFiles.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    synchronized public void setFilterList(List<MyAudioFile> filteredList) {
        listFiles = filteredList;
        notifyDataSetChanged();
    }
}
