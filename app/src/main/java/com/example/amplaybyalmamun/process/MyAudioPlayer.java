package com.example.amplaybyalmamun.process;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.amplaybyalmamun.GroupActivity;
import com.example.amplaybyalmamun.PlayAudio;
import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.custom_views.TouchableView;
import com.example.amplaybyalmamun.fragments.FragSongs;
import com.example.amplaybyalmamun.gadgets.MyViews;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.threads.BlurBgLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MyAudioPlayer{
    @SuppressLint("StaticFieldLeak")
    public static MyMediaPlayer prevPlayer = null;
    private final MyMediaPlayer myPlayer;
    public static boolean playFromPlayBar = false;
    private final Context context;
    private final Set<View> setBg_blur, setTv_playingFrom, setTv_title, setTv_artists, setTv_duration, setTv_liveDuration, setImgV_albumArt, setSeekBar, setBtn_Favorite, setTagsField;


    private final List<MyAudioFile> listFiles;
    private MyAudioFile file;
    private String title, album, artist, path;
    Bitmap bmAlbumArt;
    public static int position = -1;
    public static int prePositionGlobal = -1;
    int prePosition;
    boolean seekWasPlaying = false;
    public static boolean isComplete = false;

    public MyAudioPlayer(Context context, List<MyAudioFile> listFiles, MyMediaPlayer myPlayer, HashMap<MyViews, Set<View>> viewMap) {
        this.myPlayer = myPlayer;
        this.context = context;
        this.listFiles = listFiles;
        prePosition = (prePositionGlobal > -1) ? MyUtils.getIndex(listFiles, Store.AUDIO_FILES.get(prePositionGlobal)) : -1;

        setBg_blur = viewMap.getOrDefault(MyViews.BG_BLUR, null);
        setTv_playingFrom = viewMap.getOrDefault(MyViews.TV_PLAYING_FROM, null);
        setTv_title = viewMap.getOrDefault(MyViews.TV_TITLE, null);
        setTv_artists = viewMap.getOrDefault(MyViews.TV_ARTISTS, null);
        setTv_duration = viewMap.getOrDefault(MyViews.TV_DURATION, null);
        setTv_liveDuration = viewMap.getOrDefault(MyViews.TV_LIVE_DURATION, null);
        setImgV_albumArt = viewMap.getOrDefault(MyViews.IV_ALBUM_ART, null);
        setSeekBar = viewMap.getOrDefault(MyViews.SEEK_BAR, null);
        setBtn_Favorite = viewMap.getOrDefault(MyViews.BTN_FAVORITE, null);
        setTagsField = viewMap.getOrDefault(MyViews.TAGS_FIELD, null);

        Set<View> setTouchableViews = viewMap.getOrDefault(MyViews.TOUCHABLE_VIEW, null),
        setBtn_PlayPause = viewMap.getOrDefault(MyViews.BTN_PLAY_PAUSE,  null),
        setBtn_Next = viewMap.getOrDefault(MyViews.BTN_NEXT, null),
        setBtn_Prev = viewMap.getOrDefault(MyViews.BTN_PREV, null);


        getAndSetData();
        // check file
        if (file == null) {
            MyUtils.showProblem(context); return;}
        // check if player set
        if (!myPlayer.isSet()) myPlayer.setPlayer(path);
        else {
            if (!playFromPlayBar || myPlayer.isPlaying()) myPlayer.start();
            if (playFromPlayBar) playFromPlayBar = false;
            setSeekBars();
            updateSeekBarAndDurationText();
        }
        myPlayer.setOnPreparedListener(mp -> {
            // Start updating the SeekBar progress while the audio is playing
            if (!playFromPlayBar) myPlayer.start();
            else playFromPlayBar = false;

            if (setSeekBar != null) { setSeekBars(); updateSeekBarAndDurationText();}
        });
        // on finish media
        myPlayer.setOnCompletionListener(mp -> {
            myPlayer.pause();
            isComplete = true;
            if (!(AppSettings.repeatStatus == AppSettings.REPEAT_ONE || (AppSettings.repeatStatus == AppSettings.REPEAT_ORDER && position == listFiles.size()-1)))
                playPrevOrNext(myPlayer, 1);
        });


        // set onclick listener on action buttons
        // btn favorite
        if (setBtn_Favorite != null)
            for (View btn : setBtn_Favorite) {
                if (btn == null) continue;
                btn.setOnClickListener(v -> {
                    AppCompatImageButton btnFavorite = (AppCompatImageButton) btn;
                    DB_Helper dbHelper = new DB_Helper(context);

                    if (file.isFavorite()){ // remove from favorite
                        MyAudioFile favFile = dbHelper.getAudioItem(DB_Helper.TABLE_FAVORITES, file);

                        if (favFile == null) { // check problem
                            MyUtils.showProblem(context);
                            return;
                        }

                        btnFavorite.setImageResource(R.drawable.ic_favorite_false);
                        dbHelper.deleteAudioItem(DB_Helper.TABLE_FAVORITES, favFile.getIdDB());
                        file.setFavorite(false);
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else { // add to favorite
                        btnFavorite.setImageResource(R.drawable.ic_favorite_true);
                        dbHelper.addAudioItem(DB_Helper.TABLE_FAVORITES, listFiles.get(position));
                        file.setFavorite(true);
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                    dbHelper.close();
                });
            }
        if (setBtn_PlayPause != null)
            for (View btn : setBtn_PlayPause)
                if (btn != null)
                    btn.setOnClickListener(v -> {
                        MyUtils.setOnClickAnim(v);
                        onClickPlayPause();
                    });
        if (setBtn_Next != null)
            for (View btn : setBtn_Next)
                if (btn != null)
                    btn.setOnClickListener(v -> {
                        MyUtils.setOnClickAnim(v);
                        playPrevOrNext(myPlayer, 1);
                    });
        if (setBtn_Prev != null)
            for (View btn : setBtn_Prev)
                if (btn != null)
                    btn.setOnClickListener(v -> {
                        MyUtils.setOnClickAnim(v);
                        playPrevOrNext(myPlayer, -1);
                    });
        if (setTouchableViews != null)
            for (View tv : setTouchableViews) {
                if (tv == null) continue;
                TouchableView touchableView = (TouchableView) tv;
                touchableView.setOnTouchListener(new View.OnTouchListener() {
                    float startX, startY;
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                startX = event.getX();
                                startY = event.getY();
                                break;

                            case MotionEvent.ACTION_UP:
                                float endX = event.getX();
                                float deltaX = endX - startX;

                                float endY = event.getY();
                                float deltaY = endY - startY;

                                // Determine swipe direction based on deltaX value
                                if (deltaX > 0 && deltaX > deltaY) {
                                    // Swiped left(<==) change to the previous audio track
                                    playPrevOrNext(myPlayer, -1);
                                } else if (deltaX < 0 && deltaX < Math.min(deltaY, deltaY * -1)) {
                                    // Swiped right(==>), change to the next audio track
                                    playPrevOrNext(myPlayer, 1);
                                } else if (deltaY > 0) {
                                    ((AppCompatActivity)context).finish();
                                }
                                //System.out.println("swipe: x-"+deltaX + ", y-" + deltaY);
                                break;
                        }
                        return true;
                    }
                });
            }
    }
    public void onClickPlayPause() {
        if (myPlayer.isPlaying())
            // Pause the audio if it is playing
            myPlayer.pause();
        else
            // Start playing the audio if it is not playing
            myPlayer.start();
        updateSeekBarAndDurationText();
    }

    public void playPrevOrNext(MyMediaPlayer myPlayer, int k) {
        if(listFiles == null) return;

        if (AppSettings.shuffleStatus)
            k = MyUtils.random(listFiles.size()) * k;

        // set prePositions
        prePosition = position;
        prePositionGlobal = (position > -1) ? MyUtils.getIndex(Store.AUDIO_FILES, listFiles.get(position)) : -1;

        position = (position+k) % listFiles.size();
        // get new position
        while (position != prePosition) {
            // after left most
            if (position < 0) position = listFiles.size()-1;
            MyAudioFile audio = listFiles.get(position);
            // check is valid
            if (audio != null && !audio.getPath().isEmpty() && new File(audio.getPath()).exists())    break;
            // update
            position = (position+k) % listFiles.size();
        }

        if (position == -1) return;

        getAndSetData();
        myPlayer.reset();
        myPlayer.setPlayer(path);
    }

    public void getAndSetData() {
        // get data
        boolean isFav = false;
        List<String> tags = new ArrayList<>();
        if (position > -1 && position < listFiles.size()) {
            file = listFiles.get(position);
            file.prepare(context);
            title       = file.getTitle();
            album       = file.getAlbum();
            artist      = file.getArtists();
            path        = file.getPath();
            bmAlbumArt  = file.getAlbumArt();
            isFav       = file.isFavorite();
            tags        = file.getTagsList();
        }

        // set data
        if (setTv_playingFrom != null)
            for (View tv : setTv_playingFrom)
                if (tv != null) ((TextView)tv).setText(album);
        if (setImgV_albumArt != null)
            for (View iv : setImgV_albumArt)
                if (iv != null){
                    if (bmAlbumArt != null) ((AppCompatImageView)iv).setImageBitmap(bmAlbumArt);
                    else ((AppCompatImageView)iv).setImageResource(R.drawable.img_def_album_art);
                }
        if (setTv_title != null)
            for (View tv : setTv_title)
                if (tv != null) ((TextView)tv).setText(title);
        if (setTv_artists != null)
            for (View tv : setTv_artists)
                if (tv != null) ((TextView)tv).setText(artist);
        if (setTv_duration != null)
            for (View tv : setTv_duration)
                if (tv != null) ((TextView)tv).setText(listFiles.get(position).getDurationInTime());
        if (setBtn_Favorite != null)
            for (View btn : setBtn_Favorite) {
                if (btn != null && isFav)   ((AppCompatImageButton) btn).setImageResource(R.drawable.ic_favorite_true);
                else if (btn != null)       ((AppCompatImageButton) btn).setImageResource(R.drawable.ic_favorite_false);
            }
        // tags field
        if (setTagsField != null)
            for (View tf : setTagsField) {
                LinearLayoutCompat tagsField = (LinearLayoutCompat) tf;
                tagsField.removeAllViews();
                for (int i=0; i<tags.size(); i++) {
                    String tag = tags.get(i);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    LinearLayoutCompat root = (LinearLayoutCompat) inflater.inflate(R.layout.tag_single, tagsField, false);
                        TextView tv_tag = root.findViewById(R.id.tv_tag);
                        tv_tag.setText(tag);
                    tagsField.addView(root);
            }
        }
        // blur bg
        if (setBg_blur != null)
            for (View bg : setBg_blur)
                new BlurBgLoader(context, file, (AppCompatImageView) bg).start();

        // update recycler views ui
        FragSongs songsFrg = FragSongs.getSongsFrg();
        GroupActivity groupActivity = GroupActivity.getInstance();

        if (prePosition > -1 && !playFromPlayBar) {
            if (prePositionGlobal > -1) Store.AUDIO_FILES.get(prePositionGlobal).setPlaying(false);
            listFiles.get(prePosition).setPlaying(false);
            if (songsFrg != null)  songsFrg.notifyItemChanged(prePosition);
            if (groupActivity != null) groupActivity.notifyItemChanged(prePosition);
        }

        if (position > -1 && !playFromPlayBar) {
            int globalPosition = MyUtils.getIndex(Store.AUDIO_FILES, listFiles.get(position));

            listFiles.get(position).setPlaying(true);
            Store.AUDIO_FILES.get(globalPosition).setPlaying(true);
            if (groupActivity != null) groupActivity.notifyItemChanged(position);
            if (songsFrg != null) songsFrg.notifyItemChanged(position);

            PlayAudio.position = position;
        }
    }



    // set seek bar
    public void setSeekBars() {
        int audioDuration = myPlayer.getDuration();
        if (setSeekBar != null)
            for (View sb : setSeekBar)
                if (sb != null) {
                    SeekBar seekBar = (SeekBar) sb;
                    seekBar.setMax(audioDuration);
                    // Update the MediaPlayer's position when the user drags the SeekBar
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                myPlayer.seekTo(progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            // Pause the audio while the user is dragging the SeekBar
                            seekWasPlaying = myPlayer.isPlaying();
                            myPlayer.pause();
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            // Resume audio playback after the user stops dragging the SeekBar
                            if (seekWasPlaying) myPlayer.start();
                            updateSeekBarAndDurationText();
                        }
                    });
                }
    }

    Handler handler = new Handler();  // Create a new Handler
    private void updateSeekBarAndDurationText() {
        try {
            int crrDuration = myPlayer.getCurrentPosition();
            // check isCompete
            if (isComplete) {crrDuration = 0; isComplete = false;}
            if (setSeekBar != null)
                for (View sb : setSeekBar)
                    if (sb != null) ((SeekBar)sb).setProgress(crrDuration);

            if (setTv_liveDuration != null)
                for (View tv : setTv_liveDuration)
                    if (tv != null) ((TextView)tv).setText(MyUtils.getDurationFormatted(crrDuration));

            if (myPlayer.isPlaying()) {
                // Post a delayed action to update again after 100 milliseconds
                handler.postDelayed(this::updateSeekBarAndDurationText, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public MyMediaPlayer getMyPlayer() {
        return myPlayer;
    }
}
