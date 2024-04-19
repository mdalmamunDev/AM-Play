package com.example.amplaybyalmamun;

import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static com.example.amplaybyalmamun.gadgets.utils.Store.AUDIO_FILES;
import static com.example.amplaybyalmamun.gadgets.utils.Store.playing_queue;
import static com.example.amplaybyalmamun.gadgets.utils.MyUtils.getLatPlayedIdx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.example.amplaybyalmamun.process.AccessAudioFiles;
import com.example.amplaybyalmamun.process.AppSettings;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.process.MyAudioPlayer;
import com.example.amplaybyalmamun.process.MyMediaPlayer;
import com.example.amplaybyalmamun.gadgets.MyViews;
import com.example.amplaybyalmamun.adaptes.ViewPagerAdapter;
import com.example.amplaybyalmamun.threads.Thread_Refresh;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private  static MainActivity instance;
    private static final int REQUEST_CODE_PERMISSION = 1;


    // views
    private ConstraintLayout /*mainHeader,*/ main_contents;
    private ShimmerFrameLayout main_placeholder;
    public SwipeRefreshLayout swipeRefreshLayout;
    public ContentResolver contentResolver;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public ViewPagerAdapter adapter;
    LinearLayoutCompat playBar, playBar_left;
    SeekBar playBar_seekBar;
    public AppCompatImageView playBar_iv_albumArt;
    public TextView playBar_tv_title, playBar_tv_artist, playBar_tv_liveDuration;
    public AppCompatImageButton playBar_btn_next, playBar_btn_playPause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // assign items
        assignItems();
        main_placeholder.setVisibility(View.VISIBLE);

/*
        DB_Helper dbHelper = new DB_Helper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, DB_Helper.DATABASE_VERSION, DB_Helper.DATABASE_VERSION);
        dbHelper.close();
*/


        // access audios
        try {
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                // MyUtils.accessAudioFiles(this);
                AUDIO_FILES = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load views
        new Loader().start();


        // open settings activity
        findViewById(R.id.btn_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));


        // refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Do your refresh logic here.
            Thread_Refresh thread = new Thread_Refresh(this, swipeRefreshLayout);
            thread.setAccessAudios(true);
            thread.setRecyclerAnim(true);
            thread.start();
        });

        // play bar
        playBar_left.setOnClickListener(v -> {
            if(MyAudioPlayer.prevPlayer == null) return;

            // assigning listFile of PlayAudio before start activity
            if (playing_queue == null || playing_queue.size() == 0)
                playing_queue = AUDIO_FILES;

            Intent intent = new Intent(MainActivity.this, PlayAudio.class);
            intent.putExtra(Keys.OPEN_BY, Keys.PLAY_BAR);
            startActivity(intent);
        });


        // assign instance
        instance = this;
    }

    class Loader extends Thread {
        Context context = MainActivity.this;

        @Override
        public void run() {
            //prepare settings
            AppSettings settings = new AppSettings(context);
            settings.prepare();

            // access files
            new AccessAudioFiles(context).accessAudioFiles();

        /* prepare group lists ends */

            runOnUiThread(() -> {
                // set tab layout
                adapter = new ViewPagerAdapter(getSupportFragmentManager(), findViewById(R.id.main_search_view));
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(settings.getLastSelectedTabIdx());
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        settings.setLastSelectedTabIdx(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

            /* Play Bar */
                // set list view
                // blur bg
                PlayAudio.setBg_blur.add(findViewById(R.id.blurBg_playBar));
                // play pause btn
                PlayAudio.setBtn_playPause.add(playBar_btn_playPause);
                // next btn
                PlayAudio.setBtn_next.add(playBar_btn_next);
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
                    viewMap.put(MyViews.BTN_NEXT,           PlayAudio.setBtn_next);
                    viewMap.put(MyViews.IV_ALBUM_ART,       PlayAudio.setTv_albumArt);
                    viewMap.put(MyViews.TV_TITLE,           PlayAudio.setTv_title);
                    viewMap.put(MyViews.TV_ARTISTS,         PlayAudio.setTv_artist);
                    viewMap.put(MyViews.TV_LIVE_DURATION,   PlayAudio.setTv_liveDuration);
                    viewMap.put(MyViews.SEEK_BAR,           PlayAudio.setSeekBar);

                MyMediaPlayer mediaPlayer = new MyMediaPlayer(context, AUDIO_FILES, PlayAudio.setBtn_playPause);
                MyAudioPlayer.position = getLatPlayedIdx(context, AUDIO_FILES);
                MyAudioPlayer.playFromPlayBar = true; // for pause first time
                MyAudioPlayer player = new MyAudioPlayer(context, AUDIO_FILES, mediaPlayer, viewMap);

                // set Previous Player
                MyAudioPlayer.prevPlayer = player.getMyPlayer();

                // set seekBar
                player.setSeekBars();

            /* Play Bar End */

                Store.maxVisited = 10;
//                new Handler().postDelayed(() -> {
                    main_placeholder.setVisibility(View.GONE);
                    main_contents.setVisibility(View.VISIBLE);
//                }, 500);

            });
        }
    }

private void assignItems() {
        main_contents               = findViewById(R.id.main_contents);
        main_placeholder            = findViewById(R.id.main_placeholder);
       /* mainHeader                  = findViewById(R.id.main_header);*/
        swipeRefreshLayout          = findViewById(R.id.swipe_refresh_layout);
        tabLayout                   = findViewById(R.id.tabLay);
        viewPager                   = findViewById(R.id.viewPager);
        contentResolver             = getContentResolver();
        // Play bar items
        playBar                     = findViewById(R.id.playBar);
        playBar_left                = findViewById(R.id.playBar_left);
        playBar_seekBar             = findViewById(R.id.playBar_seekBar);
        playBar_iv_albumArt         = findViewById(R.id.playBar_iv_albumArt);
        playBar_tv_title            = findViewById(R.id.tv_title);
        playBar_tv_artist           = findViewById(R.id.playBar_tv_artist);
        playBar_tv_liveDuration     = findViewById(R.id.playBar_tv_liveDuration);
        playBar_btn_next            = findViewById(R.id.playBar_btn_next);
        playBar_btn_playPause       = findViewById(R.id.playBar_btn_playPause);
    }

    /* take permissions */
    private void requestPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent();
                intent.setAction(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.e("Permission E", "requestPermissions: ", e);
                Intent intent = new Intent();
                intent.setAction(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }

    }
    private final ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // handle the result of out intent
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            // permission granted

                            AUDIO_FILES = new ArrayList<>();
                            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), findViewById(R.id.main_search_view));
                            viewPager.setAdapter(adapter);
                            tabLayout.setupWithViewPager(viewPager);
                        } /*else {
                            // permission denied
                        }*/
                    } /*else {
                        //
                    }*/
                }
            }
    );
    // handle permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            // Check if the required permission was granted.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your operations.
                // Retrieve the audio files

                AUDIO_FILES = new ArrayList<>();

                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), findViewById(R.id.main_search_view));
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
            } /*else {
                // Permission denied, handle this situation (e.g., show an explanation, disable certain features, etc.).
                // You can inform the user or disable features that require this permission.
            }*/
        }
    }
    // check permissions
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            // below 11(R)
            return  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }



    public static MainActivity getInstance() {
        return instance;
    }

    public void setOnScrollUpViewVisibility() {
//        mainHeader.setVisibility(View.GONE);
//        playBar.setVisibility(View.VISIBLE);
    }
    public void setOnScrollDownViewVisibility() {
//        mainHeader.setVisibility(View.VISIBLE);
//        playBar.setVisibility(View.GONE);
    }
}