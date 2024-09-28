package com.example.amplaybyalmamun.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.utils.MyAudioColumns;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.process.AppSettings;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.threads.Thread_Refresh;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomMenuFragment extends BottomSheetDialogFragment {
    Context context;
    public BottomMenuFragment(Context context) {
        super();
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sort_menu_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // title
        setSortOption(view, R.id.bm_title, R.id.bm_title_icon, R.drawable.bg_gradient_border_top, MyAudioColumns.TITLE);

        // artist
        setSortOption(view, R.id.bm_artist, R.id.bm_artist_icon, R.drawable.bg_gradient_border_top, MyAudioColumns.ARTIST);

        // album
        setSortOption(view, R.id.bm_album, R.id.bm_album_icon, R.drawable.bg_gradient_border_top, MyAudioColumns.ALBUM);

        // year
        setSortOption(view, R.id.bm_year, R.id.bm_year_icon, R.drawable.bg_gradient_border_top, MyAudioColumns.YEAR);

        // date release
        setSortOption(view, R.id.bm_dateRelease, R.id.bm_dateRelease_icon, R.drawable.bg_gradient_border_top, MyAudioColumns.DATE_RELEASE);

        // date added
        setSortOption(view, R.id.bm_dateAdded, R.id.bm_dateAdded_icon, R.drawable.bg_gradient_border_top, MyAudioColumns.DATE_ADDED);

        // date added
        setSortOption(view, R.id.bm_dateModified, R.id.bm_dateModified_icon, R.drawable.bg_gradient_border_top, MyAudioColumns.DATE_MODIFIED);
    }


    private void setSortOption(View view, int optIdRes, int iconIdRes, int bgRes, String sortOrder) {

        LinearLayoutCompat option = view.findViewById(optIdRes);

        if (AppSettings.sortOrder.contains(sortOrder)) {
            AppCompatImageView icon = view.findViewById(iconIdRes);
            option.setBackgroundResource(bgRes);

            if (AppSettings.sortOrder.contains(Keys.SORT_DESC))
                icon.setImageResource(R.drawable.ic_arrow_desc);
            else icon.setImageResource(R.drawable.ic_arrow_asc);
        }

        option.setOnClickListener(v -> {
            AppSettings settings = new AppSettings(context);
            settings.setSortOrder(sortOrder, !AppSettings.sortOrder.contains(Keys.SORT_ASC));

            refresh();
            dismiss();
        });
    }

    private void refresh() {
        if(context != null) {
            Store.maxVisited = -1;
            Thread_Refresh thread = new Thread_Refresh(context);
            thread.setAccessAudios(true);
            thread.start();
        }
    }
}
