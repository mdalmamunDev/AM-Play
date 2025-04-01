package com.example.amplaybyalmamun.process;

import static com.example.amplaybyalmamun.gadgets.utils.Store.AUDIO_FILES;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

import java.util.ArrayList;
import java.util.List;

public class PlayListHandler {
    private final Context context;
    private final Resources resources;

    public PlayListHandler(Context context, Resources resources) {
        this.context = context;
        this.resources = resources;
    }

    private void showAudioListDialog(String playlistName) {
        try {
            AlertDialog.Builder audioDialogBuilder = new AlertDialog.Builder(context);
            audioDialogBuilder.setTitle("Select Audio Files");

            ListView listView = new ListView(context);
            List<String> audioFiles = getAudioFiles();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, audioFiles);
            listView.setAdapter(adapter);

            List<Integer> selectedPositions = new ArrayList<>();
            setupItemSelection(listView, selectedPositions);

            audioDialogBuilder.setView(listView);
            audioDialogBuilder.setPositiveButton("Add to Playlist", (dialog, which) -> {
                try (DB_Helper dbHelper = new DB_Helper(context)) {
                    for (int position : selectedPositions) {
                        MyAudioFile file = AUDIO_FILES.get(position);
                        file.setPlaylist(playlistName);
                        dbHelper.addAudioItem(DB_Helper.TABLE_AUDIO_PLAYLISTS, file);
                    }
                    Toast.makeText(context, "Audio files added to playlist", Toast.LENGTH_SHORT).show();
                }
                selectedPositions.clear();
            });

            audioDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            audioDialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupItemSelection(ListView listView, List<Integer> selectedPositions) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(Integer.valueOf(position));
                view.setBackgroundColor(resources.getColor(android.R.color.transparent));
            } else {
                selectedPositions.add(position);
                view.setBackgroundColor(resources.getColor(R.color.primary));
            }
        });
    }

    private List<String> getAudioFiles() {
        List<String> audioFiles = new ArrayList<>();
        for (int i = 0; i < Math.min(100, AUDIO_FILES.size()); i++) {
            audioFiles.add(AUDIO_FILES.get(i).getTitle());
        }
        return audioFiles;
    }

    public void addNew() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("New Playlist");

        EditText input = new EditText(context);
        input.setHint("Enter playlist name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setPadding(30, 30, 30, 30);
        builder.setView(input);

        builder.setPositiveButton("Next", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                showAudioListDialog(playlistName);
            } else {
                Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void addAudioTo(MyAudioFile file) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle("Add To Playlist");

            ListView listView = new ListView(context);
            List<String> playlists;
            try (DB_Helper dbHelper = new DB_Helper(context)) {
                playlists = dbHelper.getAudioPlaylists();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, playlists);
            listView.setAdapter(adapter);

            List<String> selectedPlaylists = new ArrayList<>();
            setupPlaylistSelection(listView, selectedPlaylists, playlists);

            dialogBuilder.setView(listView);
            dialogBuilder.setPositiveButton("Add to Playlist", (dialog, which) -> {
                try (DB_Helper dbHelper = new DB_Helper(context)) {
                    for (String playlist : selectedPlaylists) {
                        file.setPlaylist(playlist);
                        dbHelper.addAudioItem(DB_Helper.TABLE_AUDIO_PLAYLISTS, file);
                    }
                }
                Toast.makeText(context, "Audio file added to playlists", Toast.LENGTH_SHORT).show();
                selectedPlaylists.clear();
            });

            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            dialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupPlaylistSelection(ListView listView, List<String> selectedPlaylists, List<String> playlists) {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String playlist = playlists.get(position);
            if (selectedPlaylists.contains(playlist)) {
                selectedPlaylists.remove(playlist);
                view.setBackgroundColor(resources.getColor(android.R.color.transparent));
            } else {
                selectedPlaylists.add(playlist);
                view.setBackgroundColor(resources.getColor(R.color.primary));
            }
        });
    }
}
