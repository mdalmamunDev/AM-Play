package com.example.amplaybyalmamun.threads;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.adaptes.RecyclerAdapter;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.models.TagBarItems;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TagBarLoader extends Thread {
    Context context;
    List<MyAudioFile> audioFiles;
    List<String> clickedTags;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    LayoutInflater layoutInflater;
    LinearLayoutCompat tagsCon, tagsCon_outer;
    ShimmerFrameLayout tagsCon_outer_placeHolder;
    TextView tv_totalSongCount;
    public TagBarLoader(TagBarItems bundle) {
        this.context = bundle.getContext();
        this.audioFiles = bundle.getAudioFiles();
        this.clickedTags = bundle.getClickedTags();
        this.recyclerView = bundle.getRecyclerView();
        this.adapter = bundle.getRecyclerAdapter();
        this.layoutInflater = LayoutInflater.from(context);
        this.tagsCon = bundle.getTagsCon();
        this.tagsCon_outer = bundle.getTagsCon_outer();
        this.tagsCon_outer_placeHolder = bundle.getTagsCon_outer_placeHolder();
        this.tv_totalSongCount = bundle.getTv_totalSongCount();
    }

    static class TagNode implements Comparable<TagNode> {
        public TagNode(String tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        String tag;
        int count;

        @Override
        public int compareTo(TagNode o) {
            return o.count - this.count; // descending order
        }
    }

    @Override
    public void run() {
        new PrepareAllFiles(context, audioFiles).prepare();


        // get tags
        Map<String, Integer> tagMap = new HashMap<>();
        for (MyAudioFile file : audioFiles) {
            List<String> tagList = file.getTagsList();
            for (String tag : tagList) {
                Integer i = tagMap.getOrDefault(tag, 0);
                tagMap.put(tag, ((i != null) ? i : 0) + 1);
            }
        }

        // sel filtered list
        if (!clickedTags.isEmpty()) {
            List<MyAudioFile> filList = getFilteredList(audioFiles, clickedTags);
            recyclerView.post(() -> adapter.setFilterList(filList));
            tv_totalSongCount.post(() -> tv_totalSongCount.setText(String.valueOf(filList.size())));
        }

        PriorityQueue<TagNode> pq_sorter = new PriorityQueue<>(); // will sort tags based on count
        tagsCon.post(() -> tagsCon.removeAllViews());

        for (String tag : tagMap.keySet()) {
            Integer i = tagMap.get(tag);
            if (i == null) continue;
            pq_sorter.add(new TagNode(tag, i));
        }

        if (pq_sorter.isEmpty()) {
            TextView tv = new TextView(context);
            String text = "No Tags";
            tv.setText(text);
            tv.setTextAppearance(R.style.tag_normal);
            tagsCon.post(() -> tagsCon.addView(tv));
        } else {
            while (!pq_sorter.isEmpty()) {
                TagNode node = pq_sorter.poll();
                if (node == null) continue;

                LinearLayoutCompat tagView = (LinearLayoutCompat) layoutInflater.inflate(R.layout.tag_single_extended, tagsCon, false);

                ((TextView) tagView.findViewById(R.id.tv_tag)).setText(node.tag);
                ((TextView) tagView.findViewById(R.id.tv_count)).setText(String.valueOf(node.count));

                if (presentAt(clickedTags, node.tag) != -1)
                    tagView.setBackgroundResource(R.drawable.bg_gradient_border_button_selected);

                tagView.setOnClickListener(v -> {
                    int presentIdx = presentAt(clickedTags, node.tag);

                    if (presentIdx == -1) { // not clicked
                        clickedTags.add(node.tag);
                        tagView.setBackgroundResource(R.drawable.bg_gradient_border_button_selected);
                        for (int i = 0; i < tagView.getChildCount(); i++) {
                            ((TextView) tagView.getChildAt(i)).setTextAppearance(R.style.text_tag_selected);
                        }
                    } else { // clicked
                        clickedTags.remove(presentIdx);
                        tagView.setBackgroundResource(R.drawable.bg_gradient_border_button_normal);
                        for (int i = 0; i < tagView.getChildCount(); i++) {
                            ((TextView) tagView.getChildAt(i)).setTextAppearance(R.style.text_tag_normal);
                        }

                    }

                    List<MyAudioFile> filList = getFilteredList(audioFiles, clickedTags);
                    adapter.setFilterList(filList);
                    tv_totalSongCount.post(() -> tv_totalSongCount.setText(String.valueOf(filList.size())));
                });


                tagsCon.post(() -> tagsCon.addView(tagView));
            }
        }

        tagsCon_outer_placeHolder.post(() -> tagsCon_outer_placeHolder.setVisibility(View.GONE));
        tagsCon_outer.post(() -> tagsCon_outer.setVisibility(View.VISIBLE));
    }

    private List<MyAudioFile> getFilteredList(List<MyAudioFile> fromList, List<String> conditionTags) {
        List<MyAudioFile> filteredList = new ArrayList<>();
        for (MyAudioFile file : fromList) { ///// place list from your list
            if (isPresentAllIn(file.getTagsList(), conditionTags))
                filteredList.add(file);
        }

        return filteredList;
    }

    private boolean isPresentAllIn(List<String> fileTags, List<String> conditionTags) {
        for (String con : conditionTags)
            if (presentAt(fileTags, con) == -1) return false;
        return true;
    }

    private int presentAt(List<String> list, String item) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equalsIgnoreCase(item)) return i;
        return -1;
    }
}
