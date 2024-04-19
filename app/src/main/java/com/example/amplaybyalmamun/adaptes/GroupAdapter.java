package com.example.amplaybyalmamun.adaptes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.GroupActivity;
import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.models.ItemGroup;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.threads.Thread_LoadImg;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private final Context context;
    private List<ItemGroup> listGroups;

    public GroupAdapter(Context context, @NonNull List<ItemGroup> listGroups) {
        this.context = context;
        this.listGroups = listGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return listGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView iv_itemImg;
        private final TextView tv_itemTitle;
        private final TextView itemCount;
        private final AppCompatImageButton btn_itemModify;
        private final LinearLayoutCompat musicPlayingAnimation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_itemImg = itemView.findViewById(R.id.iv_itemImg);
            tv_itemTitle = itemView.findViewById(R.id.tv_title);
            itemCount = itemView.findViewById(R.id.playBar_tv_artist);
            btn_itemModify = itemView.findViewById(R.id.playBar_tv_duration);
            musicPlayingAnimation = itemView.findViewById(R.id.anim_music_playing);
        }

        public void bind(int position) {
            ItemGroup crrGroup = listGroups.get(position);

            // Set data to views
            tv_itemTitle.setText(crrGroup.getTitle());
            itemCount.setText(String.valueOf(crrGroup.getCount()));
            iv_itemImg.setImageResource(R.drawable.img_def_album_art);

            // Load image using Thread_LoadImg
            List<MyAudioFile> crrFiles = crrGroup.getListFiles();
            MyAudioFile f;
            if (crrFiles.size() > 0 && (f = crrFiles.get(0)) != null) {
                Thread_LoadImg threadLoadImg = new Thread_LoadImg(context, f, iv_itemImg);
                threadLoadImg.start();
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                GroupActivity.itemGroup = crrGroup;
                Intent intent = new Intent(context, GroupActivity.class);
                context.startActivity(intent);
            });

            btn_itemModify.setOnClickListener(v -> {
                // Handle modify button click
            });

            // Set music playing animation visibility
            if (crrGroup.isPlayingFromThis()) {
                musicPlayingAnimation.setVisibility(View.VISIBLE);
            } else {
                musicPlayingAnimation.setVisibility(View.GONE);
            }
        }
    }
}
