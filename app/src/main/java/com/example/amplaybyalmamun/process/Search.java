package com.example.amplaybyalmamun.process;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.example.amplaybyalmamun.adaptes.RecyclerAdapter;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.models.TagBarItems;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.threads.TagBarLoader;

import java.util.ArrayList;
import java.util.List;

public class Search {
    SearchView searchView;
    RecyclerAdapter adapter;
    List<MyAudioFile> audioFiles;
    TagBarItems bundle;

    public Search(SearchView searchView, RecyclerAdapter adapter, List<MyAudioFile> audioFiles, TagBarItems bundle) {
        this.searchView = searchView;
        this.adapter = adapter;
        this.audioFiles = audioFiles;
        this.bundle = bundle;
    }



    public void set() {
        List<MyAudioFile> filteredList = new ArrayList<>();

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList.clear();
                for (MyAudioFile file : audioFiles) {
                    if (file.getFileInfo().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(file);
                    }
                }
                adapter.setFilterList(filteredList);

                /* tag actions */
                bundle.setAudioFiles(filteredList);
                bundle.getTv_totalSongCount().setText(String.valueOf(filteredList.size()));
                bundle.getTagsCon_outer().setVisibility(View.GONE);
                bundle.getTagsCon_outer_placeHolder().setVisibility(View.VISIBLE);

                try {
                    TagBarLoader loader = new TagBarLoader(bundle);
                    loader.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /* tag actions ends */
                return true;
            }
        });
    }

}
