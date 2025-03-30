package com.example.amplaybyalmamun.process;

import static com.example.amplaybyalmamun.gadgets.utils.Store.AUDIO_FILES;

import com.example.amplaybyalmamun.gadgets.models.ItemGroup;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupGetter {

    // get all from DB as groupList of playlists
    public List<ItemGroup> getPlaylistGroup(DB_Helper dbHelper) {
        List<ItemGroup> groupList = new ArrayList<>();

        // playlist history
        ItemGroup groupHistory = new ItemGroup("History");
        groupHistory.setListFiles(dbHelper.getAllAudioItems(DB_Helper.TABLE_AUDIO_HISTORY));
        // playlist favorites
        ItemGroup groupFavorites = new ItemGroup("Favorites");
        groupFavorites.setListFiles(dbHelper.getAllAudioItems(DB_Helper.TABLE_AUDIO_FAVORITES));
        // add to store
        groupList.add(groupHistory);
        groupList.add(groupFavorites);


//        List<String> playlistTitles = new ArrayList<>();
        Map<String, List<MyAudioFile>> map = new HashMap<>();
        List<MyAudioFile> othersPlaylistAudios = dbHelper.getAllAudioItems(DB_Helper.TABLE_AUDIO_PLAYLISTS);
        for (MyAudioFile file : othersPlaylistAudios) {
            if (map.containsKey(file.getPlaylist())){
                Objects.requireNonNull(map.get(file.getPlaylist())).add(file);
            } else {
                List<MyAudioFile> list = new ArrayList<>();
                list.add(file);
                map.put(file.getPlaylist(), list);
            }
        }
        for (Map.Entry<String, List<MyAudioFile>> entry : map.entrySet()) {
            ItemGroup group = new ItemGroup(entry.getKey());
            group.setListFiles(entry.getValue());
            groupList.add(group);
        }

        return groupList;
    }

    // group artist
    public List<ItemGroup> getArtistGroup() {
        if(AUDIO_FILES == null || AUDIO_FILES.isEmpty()) return new ArrayList<>();

        HashMap<String, List<MyAudioFile>> map = new HashMap<>();

        for (MyAudioFile file : AUDIO_FILES) {
            List<String> artistList = file.getArtistList();
            for (String artist : artistList) {
                List<MyAudioFile> preAdded = map.get(artist);
                if (preAdded == null) preAdded = new ArrayList<>();
                preAdded.add(file);
                map.put(artist, preAdded);
            }
        }

        return convertItemGroupFromMap(map);
    }

    // group album
    public List<ItemGroup> getAlbumGroup() {
        if(AUDIO_FILES == null || AUDIO_FILES.isEmpty()) return new ArrayList<>();

        HashMap<String, List<MyAudioFile>> map = new HashMap<>();

        for (MyAudioFile file : AUDIO_FILES) {
            String album = file.getAlbum();
            List<MyAudioFile> preAdded = map.get(album);
            if (preAdded == null) preAdded = new ArrayList<>();
            preAdded.add(file);
            map.put(album, preAdded);
        }

        return convertItemGroupFromMap(map);
    }

    // group folder
    public List<ItemGroup> getFolderGroup() {
        if(AUDIO_FILES == null || AUDIO_FILES.isEmpty()) return new ArrayList<>();

        HashMap<String, List<MyAudioFile>> map = new HashMap<>();

        for (MyAudioFile file : AUDIO_FILES) {
            String folder = file.getDir();
            List<MyAudioFile> preAdded = map.get(folder);
            if (preAdded == null) preAdded = new ArrayList<>();
            preAdded.add(file);
            map.put(folder, preAdded);
        }

        return convertItemGroupFromMap(map);
    }

    // group genre
    public List<ItemGroup> getGenreGroup() {
        if(AUDIO_FILES == null || AUDIO_FILES.isEmpty()) return new ArrayList<>();

        HashMap<String, List<MyAudioFile>> map = new HashMap<>();

        for (MyAudioFile file : AUDIO_FILES) {
            String genre = file.getGenreFirst();
            List<MyAudioFile> preAdded = map.get(genre);
            if (preAdded == null) preAdded = new ArrayList<>();
            preAdded.add(file);
            map.put(genre, preAdded);
        }

        return convertItemGroupFromMap(map);
    }



    // convert HashMap to GroupList
    private List<ItemGroup> convertItemGroupFromMap(HashMap<String, List<MyAudioFile>> map) {
        List<ItemGroup> groupList = new ArrayList<>();

        for (String key : MyUtils.sortSet(map.keySet())) {
            ItemGroup group = new ItemGroup(key); // here key is folder name
            List<MyAudioFile> list = map.get(key);
            group.setListFiles((list == null) ? new ArrayList<>() : list);
            groupList.add(group);
        }

        return groupList;
    }
}
