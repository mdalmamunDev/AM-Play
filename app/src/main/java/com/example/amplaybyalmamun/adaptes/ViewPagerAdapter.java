package com.example.amplaybyalmamun.adaptes;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.amplaybyalmamun.fragments.FragAlbums;
import com.example.amplaybyalmamun.fragments.FragArtists;
import com.example.amplaybyalmamun.fragments.FragFolders;
import com.example.amplaybyalmamun.fragments.FragGenres;
import com.example.amplaybyalmamun.fragments.FragPlayLists;
import com.example.amplaybyalmamun.fragments.FragSongs;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    SearchView searchView;
    public ViewPagerAdapter(FragmentManager fm, SearchView searchView) {
        super(fm);
        this.searchView = searchView;
//        Log.d("loader_pr", "k: " + "position");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1: return new FragPlayLists();
            case 2: return new FragArtists();
            case 3: return new FragFolders();
            case 4: return new FragGenres();
            case 5: return new FragAlbums();
            default: return new FragSongs(searchView);
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1: return "Playlists";
            case 2: return "Artists";
            case 3: return "Folders";
            case 4: return "Genres";
            case 5: return "Albums";
            default: return "Songs";
        }
    }
}
