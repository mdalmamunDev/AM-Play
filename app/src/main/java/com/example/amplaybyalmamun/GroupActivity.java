package com.example.amplaybyalmamun;

import static com.example.amplaybyalmamun.gadgets.utils.Store.AUDIO_FILES;
import static com.example.amplaybyalmamun.gadgets.utils.Store.playing_queue;
import static com.example.amplaybyalmamun.gadgets.utils.MyUtils.getLatPlayedIdx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amplaybyalmamun.adaptes.RecyclerAdapter;
import com.example.amplaybyalmamun.custom_views.TouchableView;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.gadgets.models.ItemGroup;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.process.MyAudioPlayer;
import com.example.amplaybyalmamun.process.MyMediaPlayer;
import com.example.amplaybyalmamun.gadgets.MyViews;
import com.example.amplaybyalmamun.gadgets.models.TagBarItems;
import com.example.amplaybyalmamun.threads.TagBarLoader;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GroupActivity extends AppCompatActivity {
    List<String> clickedTags = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private static GroupActivity instance = null;

    // assign ItemGroup before start this activity
    public static ItemGroup itemGroup = new ItemGroup();
    List<MyAudioFile> listFiles;


    // views
    AppCompatImageButton playBar_btn_playPause, playBar_btn_playNext;
    LinearLayoutCompat tagsCon_outer, tagsCon, playBar_left;
    ShimmerFrameLayout tagsCon_outer_placeHolder;
    AppCompatImageView iv_cover, playBar_iv_albumArt;
    TextView tv_title, tv_itemCount, tv_itemLength, playBar_tv_title, playBar_tv_liveDuration, playBar_tv_artist;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    TouchableView playBar;
    SeekBar playBar_seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // check itemGroup
        if (itemGroup == null) {
            MyUtils.showProblem(this); finish();}

        // assign items
        assignItems();

        // set items
        setItems();
        TextView tv_totalSongCount = findViewById(R.id.tv_totalSongCount);
        if (listFiles != null) tv_totalSongCount.setText(String.valueOf(listFiles.size()));

        // set recycler view
        adapter = new RecyclerAdapter(this, listFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    /* Play Bar */
        MyMediaPlayer mediaPlayer = MyAudioPlayer.prevPlayer;
        mediaPlayer.addPlayPauseBtn(playBar_btn_playPause);
        // set list view
            // blur bg
            PlayAudio.setBg_blur.add(findViewById(R.id.blurBg_playBar));
            // play pause btn
            PlayAudio.setBtn_playPause.add(playBar_btn_playPause);
            // play next btn
            PlayAudio.setBtn_next.add(playBar_btn_playNext);
            // touchable view
            PlayAudio.set_touchableView.add(playBar);
            // album art
            PlayAudio.setTv_albumArt.add(playBar_iv_albumArt);
            // title
            PlayAudio.setTv_title.add(playBar_tv_title);
            // artist
            PlayAudio.setTv_artist.add(playBar_tv_artist);
            // live duration
            PlayAudio.setTv_liveDuration.add(playBar_tv_liveDuration);
            // seek bar
            PlayAudio.setSeekBar.add(playBar_seekBar);
        // set media player
        HashMap<MyViews, Set<View>> viewMap = new HashMap<>();
        viewMap.put(MyViews.BG_BLUR,            PlayAudio.setBg_blur);
        viewMap.put(MyViews.TOUCHABLE_VIEW,     PlayAudio.set_touchableView);
        viewMap.put(MyViews.BTN_PLAY_PAUSE,     PlayAudio.setBtn_playPause);
        viewMap.put(MyViews.IV_ALBUM_ART,       PlayAudio.setTv_albumArt);
        viewMap.put(MyViews.TV_TITLE,           PlayAudio.setTv_title);
        viewMap.put(MyViews.TV_ARTISTS,         PlayAudio.setTv_artist);
        viewMap.put(MyViews.TV_LIVE_DURATION,   PlayAudio.setTv_liveDuration);
        viewMap.put(MyViews.SEEK_BAR,           PlayAudio.setSeekBar);

        MyAudioPlayer.position = getLatPlayedIdx(this, AUDIO_FILES);
        MyAudioPlayer.playFromPlayBar = true; // for pause first time
        MyAudioPlayer player = new MyAudioPlayer(this, AUDIO_FILES, mediaPlayer, viewMap);

        // set Previous Player
        MyAudioPlayer.prevPlayer = player.getMyPlayer();


        // set seekBar
        player.setSeekBars();

        playBar_left.setOnClickListener(v -> {
            if(MyAudioPlayer.prevPlayer == null) return;

            // assigning listFile of PlayAudio before start activity
            if (playing_queue == null || playing_queue.size() == 0)
                playing_queue = AUDIO_FILES;

            Intent intent = new Intent(GroupActivity.this, PlayAudio.class);
            intent.putExtra(Keys.OPEN_BY, Keys.PLAY_BAR);
            startActivity(intent);
        });
    /* Play Bar End */

        /* tag actions */
        TagBarItems bundle = new TagBarItems(this)
                .setAudioFiles(listFiles)
                .setClickedTags(clickedTags)
                .setRecyclerView(recyclerView)
                .setRecyclerAdapter(adapter)
                .setTagsCon(tagsCon)
                .setTagsCon_outer(tagsCon_outer)
                .setTagsCon_outer_placeHolder(tagsCon_outer_placeHolder)
                .setTv_totalSongCount(tv_totalSongCount);
        TagBarLoader loader = new TagBarLoader(bundle);
        loader.start();
        /* tag actions ends */


        // btn back
        findViewById(R.id.group_btn_back).setOnClickListener(v -> {
            MyUtils.setOnClickAnim(v);
            finish();
        });

        // btn play all
        findViewById(R.id.group_btn_playAll).setOnClickListener(view -> {
            if (listFiles == null || listFiles.size() == 0) {
                Toast.makeText(this, "No file to play!", Toast.LENGTH_SHORT).show();
                return;
            }

            // set audioFiles of PlayAudio
            Store.playing_queue = this.listFiles;

            // Launch PlayActivity and pass the audioFile
            startActivity(
                    new Intent(this, PlayAudio.class)
                    .putExtra(Keys.POSITION, 0)
                    .putExtra(Keys.TOTAL_AUDIOS, listFiles.size())
            );
        });
        // btn play shuffle
        findViewById(R.id.group_btn_playShuffle).setOnClickListener(view -> {
            if (listFiles == null || listFiles.size() == 0) {
                Toast.makeText(this, "No file to play!", Toast.LENGTH_SHORT).show();
                return;
            }

            // set audioFiles of PlayAudio
            Store.playing_queue = this.listFiles;

            // Launch PlayActivity and pass the audioFile
            startActivity(
                    new Intent(this, PlayAudio.class)
                            .putExtra(Keys.POSITION, MyUtils.random(listFiles.size()))
                            .putExtra(Keys.TOTAL_AUDIOS, listFiles.size())
            );
        });

        // instance
        instance = this;
    }

    @Override
    public void finish() {
        super.finish();
        instance = null;
    }

    private void assignItems() {
        // views
        iv_cover                = findViewById(R.id.group_iv_cover);
        tv_title                = findViewById(R.id.group_tv_title);
        tv_itemCount            = findViewById(R.id.group_tv_itemCount);
        tv_itemLength           = findViewById(R.id.group_tv_itemLength);
        recyclerView            = findViewById(R.id.group_recyclerView);
        tagsCon_outer           = findViewById(R.id.tagBar_con_outer);
        tagsCon                 = findViewById(R.id.tags_con);
        tagsCon_outer_placeHolder = findViewById(R.id.tagBar_con_outer_placeholder);
        // play bar
        playBar                 = findViewById(R.id.playBar);
        playBar_left            = findViewById(R.id.playBar_left);
        playBar_seekBar         = findViewById(R.id.playBar_seekBar);
        playBar_iv_albumArt     = findViewById(R.id.playBar_iv_albumArt);
        playBar_tv_title        = findViewById(R.id.tv_title);
        playBar_tv_liveDuration = findViewById(R.id.playBar_tv_liveDuration);
        playBar_tv_artist       = findViewById(R.id.playBar_tv_artist);
        playBar_btn_playPause   = findViewById(R.id.playBar_btn_playPause);
        playBar_btn_playNext    = findViewById(R.id.playBar_btn_next);

        listFiles               = itemGroup.getListFiles();
    }
    private void setItems() {
        // cover img
        MyAudioFile crrFile;
        if (listFiles.size() > 0 && (crrFile = listFiles.get(0)) != null) {
            crrFile.prepare(this);
            Bitmap bm = crrFile.getAlbumArt();
            if (bm != null) iv_cover.setImageBitmap(bm);
            else            iv_cover.setImageResource(R.drawable.img_def_album_art);
        }
        // title
        tv_title.setText(itemGroup.getTitle());
        // item count
        String itemCount = itemGroup.getCount() + " Songs";
        tv_itemCount.setText(itemCount);
        // item length
        tv_itemLength.setText(MyUtils.getDurationFormatted(itemGroup.getLength()));
    }

    public static GroupActivity getInstance() {
        return instance;
    }
    public void notifyItemChanged(int position) {
        adapter.notifyItemChanged(position);
    }
}