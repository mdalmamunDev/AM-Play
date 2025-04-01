package com.example.amplaybyalmamun.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.adaptes.GroupAdapter;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.process.DB_Helper;
import static com.example.amplaybyalmamun.gadgets.utils.Store.AUDIO_FILES;
import com.example.amplaybyalmamun.process.GroupGetter;
import com.example.amplaybyalmamun.gadgets.models.ItemGroup;
import com.example.amplaybyalmamun.process.PlayListHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragPlayLists extends Fragment {
    RecyclerView recyclerView;


    public FragPlayLists() {
        super();
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        recyclerView = view.findViewById(R.id.group_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        // load data
        new Loader().start();


        FloatingActionButton addBtn = view.findViewById(R.id.group_btn_add);
        addBtn.setVisibility(View.VISIBLE);
        PlayListHandler handler = new PlayListHandler(getContext(), getResources());
        addBtn.setOnClickListener(v -> {
            handler.addNew();
        });
        return view;
    }
/***   onCreateView end  ***/
    class Loader extends Thread{
        @Override
        public void run() {
            DB_Helper dbHelper = new DB_Helper(getContext());
            GroupGetter getter = new GroupGetter();
            List<ItemGroup> groupList = getter.getPlaylistGroup(dbHelper);
            dbHelper.close();

            recyclerView.post(() -> {
                GroupAdapter recyclerAdapter = new GroupAdapter(getContext(), groupList);
                recyclerView.setAdapter(recyclerAdapter);
            });
        }
    }
}