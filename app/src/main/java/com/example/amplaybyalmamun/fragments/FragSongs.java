package com.example.amplaybyalmamun.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.MainActivity;
import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.adaptes.RecyclerAdapter;
import com.example.amplaybyalmamun.gadgets.models.TagBarItems;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.process.Search;
import com.example.amplaybyalmamun.threads.TagBarLoader;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class FragSongs extends Fragment {
    List<String> clickedTags = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private static FragSongs instance;
    SearchView searchView;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    TextView tv_totalSongCount;
    LinearLayoutCompat tagsCon_outer, tagsCon;
    ShimmerFrameLayout tagsCon_outer_placeHolder;

    public FragSongs() {
        // Required empty public constructor
    }
    public FragSongs(SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        // assign items
        recyclerView = view.findViewById(R.id.group_recyclerView);
        MainActivity mainActivity = (MainActivity) getActivity();
        AppCompatImageButton btnSort = view.findViewById(R.id.btn_sort);
        tv_totalSongCount = view.findViewById(R.id.tv_totalSongCount);
        tagsCon_outer = view.findViewById(R.id.tagBar_con_outer);
        tagsCon_outer_placeHolder = view.findViewById(R.id.tagBar_con_outer_placeholder);
        tagsCon = view.findViewById(R.id.tags_con);

        // set total songs count
        setTotalSongsCount();


    /* recycler */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        // Check if the context is not null before creating the adapter
        if (getContext() != null) {
            adapter = new RecyclerAdapter(getContext(), Store.AUDIO_FILES);
            recyclerView.setAdapter(adapter);
        }
        // Add scroll listener to RecyclerView
        final int[] state = new int[1];
        if (mainActivity != null)
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    state[0] = newState;
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 5 && (state[0] == RecyclerView.SCROLL_STATE_IDLE || state[0] == RecyclerView.SCROLL_STATE_SETTLING)) {
                        // Scrolling down
                        mainActivity.setOnScrollUpViewVisibility();
                    } else if (dy < -5 && (state[0] == RecyclerView.SCROLL_STATE_IDLE || state[0] == RecyclerView.SCROLL_STATE_SETTLING)){
                        // Scrolling up or at top
                        mainActivity.setOnScrollDownViewVisibility();
                    }
                }
            });
    /* recycler end */


        // sort dialog
        btnSort.setOnClickListener(v -> {
            if (mainActivity == null) return;

            BottomMenuFragment bottomMenuFragment = new BottomMenuFragment(getContext());
            bottomMenuFragment.show(mainActivity.getSupportFragmentManager(), bottomMenuFragment.getTag());
        });


       /* tag actions */
        TagBarItems bundle = new TagBarItems(getContext())
                .setAudioFiles(Store.AUDIO_FILES)
                .setClickedTags(clickedTags)
                .setRecyclerView(recyclerView)
                .setRecyclerAdapter(adapter)
                .setTagsCon(tagsCon)
                .setTagsCon_outer(tagsCon_outer)
                .setTagsCon_outer_placeHolder(tagsCon_outer_placeHolder)
                .setTv_totalSongCount(tv_totalSongCount);
        TagBarLoader loader = new TagBarLoader(bundle);
        loader.start();
       /* tag actions ends */


        // search file
        new Search(searchView, adapter, Store.AUDIO_FILES, bundle).set();


        // get instance
        instance = this;

        return view;
    }

    /*     public void resetRecycler(List<MyAudioFile> listAudio) {
            // Update the RecyclerView on the main thread
            adapter.setFilterList(listAudio);      }

    /*    public void notifyItemInserted(int position) {
    //        synchronized (adapter) {
                adapter.notifyItemInserted(position);
    //        }
        }*/
    public void notifyItemChanged(int position) {
        adapter.notifyItemChanged(position);
    }


    public void setTotalSongsCount() {
        if (Store.AUDIO_FILES == null) return;
        tv_totalSongCount.setText(String.valueOf(Store.AUDIO_FILES.size()));
    }

    public static FragSongs getSongsFrg() {
        return instance;
    }
}