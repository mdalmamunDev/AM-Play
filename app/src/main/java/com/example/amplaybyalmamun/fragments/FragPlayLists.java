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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FragPlayLists extends Fragment {
    RecyclerView recyclerView;
    private final ArrayList<Integer> selectedAudioPositions = new ArrayList<>();


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
        addBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("New Playlist");

            final EditText input = new EditText(getContext());
            input.setHint("Enter playlist name");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setPadding(30, 30, 30, 30);
            builder.setView(input);

            builder.setPositiveButton("Next", (dialog, which) -> {
                String playlistName = input.getText().toString().trim();
                if (!playlistName.isEmpty()) {
                    showAudioListDialog(playlistName);
                } else {
                    Toast.makeText(getContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
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

    private void showAudioListDialog(String playlistName) {
        try {
            AlertDialog.Builder audioDialogBuilder = new AlertDialog.Builder(getContext());
            audioDialogBuilder.setTitle("Select Audio Files");

            ListView listView = new ListView(getContext());
            List<String> audioFiles = getAudioFiles(); // Replace with your method to get audio files
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, audioFiles);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (selectedAudioPositions.contains(position)) {
                    selectedAudioPositions.remove(Integer.valueOf(position));
                    view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                } else {
                    selectedAudioPositions.add(position);
                    view.setBackgroundColor(getResources().getColor(R.color.primary));
                }
            });

            audioDialogBuilder.setView(listView);

            audioDialogBuilder.setPositiveButton("Add to Playlist", (dialog, which) -> {
                DB_Helper dbHelper = new DB_Helper(requireContext());
                for (Integer p : selectedAudioPositions) {
                    MyAudioFile file = AUDIO_FILES.get(p);
                    file.setPlaylist(playlistName);
                    dbHelper.addAudioItem(DB_Helper.TABLE_AUDIO_PLAYLISTS, file);
                }
                dbHelper.close();
                dialog.dismiss();
                Toast.makeText(getContext(), "Audio files added to playlist", Toast.LENGTH_SHORT).show();
                selectedAudioPositions.clear();
            });

            audioDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            audioDialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Dummy method to return a list of audio files (replace with actual data)
    private List<String> getAudioFiles() {
        List<String> audioFiles = new ArrayList<>();
        for (int i = 0; i<100 && i<AUDIO_FILES.size() ; i++) {
            audioFiles.add(AUDIO_FILES.get(i).getTitle());
        }
        return audioFiles;
    }
}