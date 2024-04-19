package com.example.amplaybyalmamun.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.adaptes.GroupAdapter;
import com.example.amplaybyalmamun.process.GroupGetter;
import com.example.amplaybyalmamun.gadgets.models.ItemGroup;

import java.util.List;

public class FragFolders extends Fragment {
    RecyclerView recyclerView;


    public FragFolders() {
        super();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);


        // set recycler view
        recyclerView = view.findViewById(R.id.group_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // load data
        new Loader().start();

        return view;
    }

    class Loader extends Thread{
        @Override
        public void run() {
            List<ItemGroup> groupList = new GroupGetter().getFolderGroup();

            recyclerView.post(() -> {
                GroupAdapter recyclerAdapter = new GroupAdapter(getContext(), groupList);
                recyclerView.setAdapter(recyclerAdapter);
            });
        }
    }
}