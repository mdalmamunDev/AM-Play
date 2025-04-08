package com.example.amplaybyalmamun;


import static com.example.amplaybyalmamun.gadgets.utils.Store.AUDIO_FILES;
import static com.example.amplaybyalmamun.gadgets.utils.Store.playing_queue;
import static com.example.amplaybyalmamun.gadgets.utils.Store.position;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;

import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.process.AppSettings;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.process.MyMusicPlayer;
import com.example.amplaybyalmamun.gadgets.MyViews;
import com.example.amplaybyalmamun.process.PlayListHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class PlayMusicActivity extends AppCompatActivity {

    MyAudioFile file;
    AppSettings settings;
    boolean isFav, shuffleStatus;
    int repeatStatus;

    // views
    public static Set<View> setBg_blur = new HashSet<>(),
                            setBtn_playPause = new HashSet<>(),
                            setBtn_next = new HashSet<>(),
                            set_touchableView = new HashSet<>(),
                            setTv_albumArt = new HashSet<>(),
                            setTv_title = new HashSet<>(),
                            setTv_artist = new HashSet<>(),
                            setTv_liveDuration = new HashSet<>(),
                            setSeekBar = new HashSet<>();
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    AppCompatImageView iv_albumArt;
    TextView tv_PlayingFrom, tv_title, tv_artists, tv_liveDuration, tv_duration;
    AppCompatImageButton btnBack, btnFavorite, btnRepeat, btnPlayPause, btnPrev, btnNext, btnShuffle, btnMoreOptions, btnAddToPlaylist;
    LinearLayoutCompat tagsField;
    int totalAudios;
    String activity_open_by = "";


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        // check if audiFiles
        if (playing_queue == null || playing_queue.isEmpty()) {finish(); return;}

        // get MainActivity instance
//        MainActivity mainActivity = MainActivity.getInstance();

        // Check if the Intent has extras
        Intent intent = getIntent();
        if (intent != null) {
            totalAudios = intent.getIntExtra(Keys.TOTAL_AUDIOS, 0);
            activity_open_by = intent.getStringExtra(Keys.OPEN_BY);
            if (activity_open_by != null && activity_open_by.equals(Keys.PLAY_BAR))
                    position = MyUtils.getLatPlayedIdx(this, playing_queue);
            else    position = intent.getIntExtra(Keys.POSITION, -1);
        }


        // assign items
        assignItems(); // assign items after get position

        // check current file
        if (file == null) {finish(); return;}

        // set items
        setData();

        // marqueeText
        tv_title.setSelected(true);


    /* * Media Player * */
        // set list of views
        Set<View> setTv_playingFrom = new HashSet<>(),
                setTv_duration = new HashSet<>(),
                setBtn_favorite = new HashSet<>(),
                setBtn_prev = new HashSet<>(),
                setTagField = new HashSet<>();

        setBg_blur.add(findViewById(R.id.blurBg_playBar));
        setTv_playingFrom.add(tv_PlayingFrom);
        set_touchableView.add(findViewById(R.id.touchableView));
        setTv_albumArt.add(iv_albumArt);
        setTv_title.add(tv_title);
        setTv_artist.add(tv_artists);
        setTv_duration.add(tv_duration);
        setTv_liveDuration.add(tv_liveDuration);
        setSeekBar.add(seekBar);
        setBtn_favorite.add(btnFavorite);
        setBtn_playPause.add(btnPlayPause);
        setBtn_next.add(btnNext);
        setBtn_prev.add(btnPrev);
        setTagField.add(tagsField);



        // check
//        MyAudioPlayer.position = position;
        // set media player
        HashMap<MyViews, Set<View>> viewMap = new HashMap<>();
        viewMap.put(MyViews.BG_BLUR, setBg_blur);
        viewMap.put(MyViews.TV_PLAYING_FROM,    setTv_playingFrom);
        viewMap.put(MyViews.TOUCHABLE_VIEW,     set_touchableView);
        viewMap.put(MyViews.IV_ALBUM_ART,       setTv_albumArt);
        viewMap.put(MyViews.TV_TITLE,           setTv_title);
        viewMap.put(MyViews.TV_ARTISTS,         setTv_artist);
        viewMap.put(MyViews.TV_DURATION,        setTv_duration);
        viewMap.put(MyViews.TV_LIVE_DURATION,   setTv_liveDuration);
        viewMap.put(MyViews.SEEK_BAR,           setSeekBar);
        viewMap.put(MyViews.BTN_FAVORITE,       setBtn_favorite);
        viewMap.put(MyViews.BTN_PLAY_PAUSE,     setBtn_playPause);
        viewMap.put(MyViews.BTN_NEXT,           setBtn_next);
        viewMap.put(MyViews.BTN_PREV,           setBtn_prev);
        viewMap.put(MyViews.TAGS_FIELD,         setTagField);

        MyMusicPlayer player = new MyMusicPlayer(this, viewMap);

        // Register the receiver
        IntentFilter filter = new IntentFilter(Keys.MUSIC_STATE_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(player, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(player, filter);
        }
    /* * Media Player End * */

    // add to playlist
        PlayListHandler playListHandler = new PlayListHandler(PlayMusicActivity.this, getResources());
        btnAddToPlaylist.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            playListHandler.addAudioTo(AUDIO_FILES.get(position));
        });

    // repeat
        btnRepeat.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);

            if (repeatStatus == AppSettings.REPEAT_ALL) { // all => one
                settings.setRepeatStatus(AppSettings.REPEAT_ONE);
                btnRepeat.setImageResource(R.drawable.ic_repeat_one);
                Toast.makeText(this, "Repeat current", Toast.LENGTH_LONG).show();
            } else if (repeatStatus == AppSettings.REPEAT_ONE) { // one => order
                settings.setRepeatStatus(AppSettings.REPEAT_ORDER);
                btnRepeat.setImageResource(R.drawable.ic_repeat_order);
                Toast.makeText(this, "Repeat order", Toast.LENGTH_LONG).show();
            } else { // order => all
                settings.setRepeatStatus(AppSettings.REPEAT_ALL);
                btnRepeat.setImageResource(R.drawable.ic_repeat_all);
                Toast.makeText(this, "Repeat all", Toast.LENGTH_LONG).show();
            }

            // update
            repeatStatus = settings.getRepeatStatus();
        });


    // shuffle
        btnShuffle.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);

            if (shuffleStatus) {
                settings.setShuffleStatus(false);
                btnShuffle.setImageResource(R.drawable.ic_shuffle_light);
                Toast.makeText(this, "Shuffle off", Toast.LENGTH_LONG).show();
            } else {
                settings.setShuffleStatus(true);
                btnShuffle.setImageResource(R.drawable.ic_shuffle);
                Toast.makeText(this, "Shuffle on", Toast.LENGTH_LONG).show();
            }
            // update
            shuffleStatus = settings.getShuffleStatus();
        });

    // back
        btnBack.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            finish();
//            startMusicService(MusicService.ACTION_PLAY);
        });

    // more options
        btnMoreOptions.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            PopupMenu popup = new PopupMenu(PlayMusicActivity.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.play_audio_dropdown, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_md) { // edit md
                    startEditMetadataActivity();
                } else if (item.getItemId() == R.id.delete) { // delete
                    new AlertDialog.Builder(this)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this file?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                if (file.delete()) {
                                    Toast.makeText(PlayMusicActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    AUDIO_FILES.remove(position);
                                    btnNext.callOnClick();
                                    btnPrev.callOnClick();
                                } else {
                                    Toast.makeText(PlayMusicActivity.this, "Error deleting file", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

                return true;
            });

            popup.show();
        });

        // by tv_title area
        findViewById(R.id.titleArtist_area).setOnClickListener(v -> startEditMetadataActivity());
    }
    /*****  onCreate end *****/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
    private void assignItems() {
        // assign items
        tv_PlayingFrom      = findViewById(R.id.textView_PlayingFromAlbum);
        iv_albumArt         = findViewById(R.id.iv_itemImg);
        tv_title            = findViewById(R.id.tv_title);
        tv_artists          = findViewById(R.id.tv_artists);
        btnBack             = findViewById(R.id.btnBack);
        btnFavorite         = findViewById(R.id.btnFavorite);
        btnRepeat           = findViewById(R.id.btnRepeat);
        btnPlayPause        = findViewById(R.id.btnPlayPause);
        btnPrev             = findViewById(R.id.btnPrev);
        btnNext             = findViewById(R.id.btnNext);
        btnShuffle          = findViewById(R.id.btnShuffle);
        btnMoreOptions     = findViewById(R.id.btnMoreOptions);
        btnAddToPlaylist    = findViewById(R.id.btn_addToPlaylist);
        seekBar             = findViewById(R.id.seekBar);
        tv_liveDuration     = findViewById(R.id.textView_liveDuration);
        tv_duration         = findViewById(R.id.playBar_tv_duration);
        tagsField = findViewById(R.id.tags_con);

        // settings
        settings            =  new AppSettings(PlayMusicActivity.this);
        shuffleStatus       = settings.getShuffleStatus();
        repeatStatus        = settings.getRepeatStatus();

        // others
        file                = playing_queue.get(position);
        isFav               = file.isFavorite();
    }
    private void setData() {
        // repeat
        if (repeatStatus == AppSettings.REPEAT_ONE)         btnRepeat.setImageResource(R.drawable.ic_repeat_one);
        else if (repeatStatus == AppSettings.REPEAT_ORDER)  btnRepeat.setImageResource(R.drawable.ic_repeat_order);
        else /*........................................ */  btnRepeat.setImageResource(R.drawable.ic_repeat_all);

        // shuffle
        if (shuffleStatus)  btnShuffle.setImageResource(R.drawable.ic_shuffle);
        else                btnShuffle.setImageResource(R.drawable.ic_shuffle_light);
    }


    private void startEditMetadataActivity() {
        Intent i = new Intent(PlayMusicActivity.this, EditAudioMetadata.class);
        i.putExtra(Keys.POSITION, MyUtils.getIndex(Store.AUDIO_FILES, playing_queue.get(position)));
        PlayMusicActivity.this.startActivity(i);
    }
}