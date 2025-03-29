package com.example.amplaybyalmamun;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.process.AppSettings;
import com.example.amplaybyalmamun.process.DB_Helper;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;

public class PlayAudio extends AppCompatActivity {

//    MyMediaPlayer audioPlayer;
//    int repeatStatus;

    // views
//    private final Set<View> setBg_blur = new HashSet<>(),
//                            setBtn_playPause = new HashSet<>(),
//                            setBtn_next = new HashSet<>(),
//                            set_touchableView = new HashSet<>(),
//                            setTv_albumArt = new HashSet<>(),
//                            setTv_title = new HashSet<>(),
//                            setTv_artist = new HashSet<>(),
//                            setTv_liveDuration = new HashSet<>(),
//                            setSeekBar = new HashSet<>();
//    private SeekBar seekBar;
//    private final Handler handler = new Handler();
//    AppCompatImageView iv_albumArt;
//    AppCompatImageButton btnBack, btnFavorite, btnPlayPause, btnPrev, btnNext, btnEditMetadata;
//    public static int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        Intent intent = getIntent();
        int id = intent.getIntExtra(Keys.ID_DB, 0);
        DB_Helper dbHelper = new DB_Helper(this);
        MyAudioFile file = dbHelper.getAudioItem(DB_Helper.TABLE_AUDIOS, id);
        dbHelper.close();

        System.out.println("4444444444444 "+ file.getPath());



    /* assign items */
        AppSettings settings = new AppSettings(PlayAudio.this);
        boolean shuffleStatus = settings.getShuffleStatus();
        boolean isFav = file.isFavorite();
        // views
        AppCompatImageButton btnRepeat = findViewById(R.id.btnRepeat);
        AppCompatImageButton btnShuffle = findViewById(R.id.btnShuffle);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_artists = findViewById(R.id.tv_artists);
        TextView tv_PlayingFrom = findViewById(R.id.textPlayingFrom);
        TextView tv_liveDuration = findViewById(R.id.textView_liveDuration);
        TextView tv_duration = findViewById(R.id.playBar_tv_duration);
        LinearLayoutCompat tagsField = findViewById(R.id.tags_con);


    /* player */
        ExoPlayer player = new ExoPlayer.Builder(PlayAudio.this).build();
        MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(file.getFile()));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        // controls
        AppCompatImageButton btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(v -> {
            MyUtils.setOnClickAnim(btnPlayPause);
            if (player.isPlaying()) {
                player.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play_40);
            } else {
                player.play();
                btnPlayPause.setImageResource(R.drawable.ic_pause_40);
            }
        });

    /* set items */
        //ui
        tv_title.setText(file.getTitle());
        tv_artists.setText(file.getArtists());
        tv_PlayingFrom.setText(file.getAlbum());
        tv_duration.setText(file.getDurationInTime());
        // repeat
        int repeatStatus = settings.getRepeatStatus();;
        if (repeatStatus == AppSettings.REPEAT_ONE)         btnRepeat.setImageResource(R.drawable.ic_repeat_one);
        else if (repeatStatus == AppSettings.REPEAT_ORDER)  btnRepeat.setImageResource(R.drawable.ic_repeat_order);
        else /*........................................ */  btnRepeat.setImageResource(R.drawable.ic_repeat_all);
        // shuffle
        if (shuffleStatus)  btnShuffle.setImageResource(R.drawable.ic_shuffle);
        else                btnShuffle.setImageResource(R.drawable.ic_shuffle_light);
//
//        // marqueeText
        tv_title.setSelected(true);



    /* * Media Player * */
//        // set list of views
//        Set<View> setTv_playingFrom = new HashSet<>(),
//                setTv_duration = new HashSet<>(),
//                setBtn_favorite = new HashSet<>(),
//                setBtn_prev = new HashSet<>(),
//                setTagField = new HashSet<>();
//
//        // blur bg
//        setBg_blur.add(findViewById(R.id.blurBg_playBar));
//        // playing from
//        setTv_playingFrom.add(tv_PlayingFrom);
//        // touchable view
//        set_touchableView.add(findViewById(R.id.touchableView));
//        // album art
//        setTv_albumArt.add(iv_albumArt);
//        // title
//        setTv_title.add(tv_title);
//        // artist
//        setTv_artist.add(tv_artists);
//        // duration
//        setTv_duration.add(tv_duration);
//        // live duration
//        setTv_liveDuration.add(tv_liveDuration);
//        // seek bar
//        setSeekBar.add(seekBar);
//        // btn favorite
//        setBtn_favorite.add(btnFavorite);
//        // btn play pause
//        setBtn_playPause.add(btnPlayPause);
//        // btn next
//        setBtn_next.add(btnNext);
//        // btn prev
//        setBtn_prev.add(btnPrev);
//        // tag field
//        setTagField.add(tagsField);



        // check
//        if(activity_open_by != null && activity_open_by.equals(Keys.PLAY_BAR) && MyAudioPlayer.prevPlayer != null) {
//            // set position
//            MyAudioPlayer.position = position;
//            // it's need to add playPause buttons again
//            MyAudioPlayer.prevPlayer.setPlayPauseBtns(setBtn_playPause);
//            audioPlayer = MyAudioPlayer.prevPlayer;
//        } else {
//            // reset positions
//            MyAudioPlayer.prePositionGlobal = MyUtils.getLatPlayedIdx(this, Store.AUDIO_FILES);
//            MyAudioPlayer.position = position;
//            // reset Previous Player
//            if (MyAudioPlayer.prevPlayer != null) {
//                MyAudioPlayer.prevPlayer.release();
//                MyAudioPlayer.prevPlayer = null;
//            }
//            audioPlayer = new MyMediaPlayer(this, playing_queue, setBtn_playPause);
//        }
//        // set media player
//        HashMap<MyViews, Set<View>> viewMap = new HashMap<>();
//        viewMap.put(MyViews.BG_BLUR, setBg_blur);
//        viewMap.put(MyViews.TV_PLAYING_FROM,    setTv_playingFrom);
//        viewMap.put(MyViews.TOUCHABLE_VIEW,     set_touchableView);
//        viewMap.put(MyViews.IV_ALBUM_ART,       setTv_albumArt);
//        viewMap.put(MyViews.TV_TITLE,           setTv_title);
//        viewMap.put(MyViews.TV_ARTISTS,         setTv_artist);
//        viewMap.put(MyViews.TV_DURATION,        setTv_duration);
//        viewMap.put(MyViews.TV_LIVE_DURATION,   setTv_liveDuration);
//        viewMap.put(MyViews.SEEK_BAR,           setSeekBar);
//        viewMap.put(MyViews.BTN_FAVORITE,       setBtn_favorite);
//        viewMap.put(MyViews.BTN_PLAY_PAUSE,     setBtn_playPause);
//        viewMap.put(MyViews.BTN_NEXT,           setBtn_next);
//        viewMap.put(MyViews.BTN_PREV,           setBtn_prev);
//        viewMap.put(MyViews.TAGS_FIELD,         setTagField);
//
//        MyAudioPlayer player = new MyAudioPlayer(this, playing_queue, audioPlayer, viewMap);
//
//        // set Previous Player
//        MyAudioPlayer.prevPlayer = player.getMyPlayer();
    /* * Media Player End * */

    // repeat
//        btnRepeat.setOnClickListener(v -> {
//            MyUtils.setOnClickAnim(v);
//
//            if (repeatStatus == AppSettings.REPEAT_ALL) { // all => one
//                settings.setRepeatStatus(AppSettings.REPEAT_ONE);
//                btnRepeat.setImageResource(R.drawable.ic_repeat_one);
//                Toast.makeText(this, "Repeat current", Toast.LENGTH_LONG).show();
//            } else if (repeatStatus == AppSettings.REPEAT_ONE) { // one => order
//                settings.setRepeatStatus(AppSettings.REPEAT_ORDER);
//                btnRepeat.setImageResource(R.drawable.ic_repeat_order);
//                Toast.makeText(this, "Repeat order", Toast.LENGTH_LONG).show();
//            } else { // order => all
//                settings.setRepeatStatus(AppSettings.REPEAT_ALL);
//                btnRepeat.setImageResource(R.drawable.ic_repeat_all);
//                Toast.makeText(this, "Repeat all", Toast.LENGTH_LONG).show();
//            }
//
//            // update
//            repeatStatus = settings.getRepeatStatus();
//        });


    // shuffle
//        btnShuffle.setOnClickListener(v -> {
//            MyUtils.setOnClickAnim(v);
//
//            if (shuffleStatus) {
//                settings.setShuffleStatus(false);
//                btnShuffle.setImageResource(R.drawable.ic_shuffle_light);
//                Toast.makeText(this, "Shuffle off", Toast.LENGTH_LONG).show();
//            } else {
//                settings.setShuffleStatus(true);
//                btnShuffle.setImageResource(R.drawable.ic_shuffle);
//                Toast.makeText(this, "Shuffle on", Toast.LENGTH_LONG).show();
//            }
//            // update
//            shuffleStatus = settings.getShuffleStatus();
//        });

    // back
//        btnBack.setOnClickListener(v -> {
//            MyUtils.setOnClickAnim(v);
//            finish();
//        });

    // edit metadata
        // by btn
//        btnEditMetadata.setOnClickListener(v -> {
//            MyUtils.setOnClickAnim(v);
//            startEditMetadataActivity();
////            showPopupMenu(v);
//        });
        // by tv_title area
        //findViewById(R.id.titleArtist_area).setOnClickListener(v -> startEditMetadataActivity());


//        dbHelper.close();
    }
    /*****  onCreate end *****/


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacksAndMessages(null);
    }
//    private void assignItems() {
//        // assign items
//        tv_PlayingFrom      = findViewById(R.id.textView_PlayingFromAlbum);
//        iv_albumArt         = findViewById(R.id.iv_itemImg);
//        tv_artists          = findViewById(R.id.tv_artists);
//        btnBack             = findViewById(R.id.btnBack);
//        btnFavorite         = findViewById(R.id.btnFavorite);
//        btnPlayPause        = findViewById(R.id.btnPlayPause);
//        btnPrev             = findViewById(R.id.btnPrev);
//        btnNext             = findViewById(R.id.btnNext);
//        btnEditMetadata     = findViewById(R.id.btnEditMetadata);
//        seekBar             = findViewById(R.id.seekBar);
//        tv_liveDuration     = findViewById(R.id.textView_liveDuration);
//        tv_duration         = findViewById(R.id.playBar_tv_duration);
//        tagsField = findViewById(R.id.tags_con);
//    }


    private void startEditMetadataActivity() {
        Intent i = new Intent(PlayAudio.this, EditAudioMetadata.class);
//        i.putExtra(Keys.POSITION, MyUtils.getIndex(Store.AUDIO_FILES, playing_queue.get(position)));
        PlayAudio.this.startActivity(i);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.play_audio_dropdown, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.edit_metadata:
//                    Toast.makeText(PlayAudio.this, "Action 1 clicked", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.delete_file:
//                    Toast.makeText(PlayAudio.this, "Action 2 clicked", Toast.LENGTH_SHORT).show();
//                    return true;
//                default:
//                    return false;
//            }
//        });
        popupMenu.show();
    }
}