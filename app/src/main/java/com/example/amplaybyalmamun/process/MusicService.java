package com.example.amplaybyalmamun.process;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;

import static com.example.amplaybyalmamun.gadgets.utils.Store.playing_queue;
import static com.example.amplaybyalmamun.gadgets.utils.Store.position;

import java.util.Random;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private int musicState = Keys.STATE_STOPPED;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            Toast.makeText(this, "Failed To Start Music Service", Toast.LENGTH_SHORT).show();
            return START_NOT_STICKY;
        }

        switch (intent.getAction()) {
            case Keys.ACTION_PLAY:
                playMusic();
                break;
            case Keys.ACTION_PAUSE:
                pauseMusic();
                break;
            case Keys.ACTION_STOP:
                stopMusic();
                break;
            case Keys.ACTION_NEXT:
                prevOrNext(1);
                break;
            case Keys.ACTION_PREV:
                prevOrNext(-1);
                break;
            case Keys.ACTION_SEEK: // Handle Seek action from Activity
                int seekPosition = intent.getIntExtra(Keys.EXTRA_PROGRESS, 0);
                seekToPosition(seekPosition);
                break;
        }

        startForeground(1, createNotification());
        return START_STICKY;
    }


    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                sendMusicBroadcast(currentPosition);
                // Repeat this task every 1000 ms (1 second)
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void playMusic() {
        if (mediaPlayer == null) {
            loadTrack(position);
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            musicState = Keys.STATE_PLAYING;
            sendMusicBroadcast();

            handler.post(updateSeekBarRunnable);
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            musicState = Keys.STATE_PAUSED;
            sendMusicBroadcast();
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || musicState == Keys.STATE_PAUSED)) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            musicState = Keys.STATE_STOPPED;
            sendMusicBroadcast();
            handler.removeCallbacks(updateSeekBarRunnable);
            stopSelf();
        }
    }

    private void prevOrNext(int k) {
        //  k: 1 = next & -1 = prev
        position = AppSettings.shuffleStatus ?
                new Random().nextInt(playing_queue.size()) :
                (position + k) % playing_queue.size();
        restartTrack();
    }


    private void seekToPosition(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
//
//            if (musicState == Keys.STATE_PLAYING) mediaPlayer.start();
//            else if (musicState == Keys.STATE_PAUSED) mediaPlayer.pause();
        }
    }

    private void restartTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        loadTrack(position);
        playMusic();
    }

    private void loadTrack(int index) {
        MyAudioFile file = playing_queue.get(index);
        if (!file.exists()) {
            MyUtils.showProblem(getApplicationContext());
            prevOrNext(1); // try the next
            return;
        }
        mediaPlayer = MediaPlayer.create(this, Uri.parse(file.getPath()));
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(mp -> {

            boolean isLastTrack = position == playing_queue.size() - 1;
            if (AppSettings.repeatStatus == AppSettings.REPEAT_ONE) {
                restartTrack(); // Replay same track
            } else if (AppSettings.repeatStatus == AppSettings.REPEAT_ORDER && isLastTrack) {
                stopMusic(); // Stop at end of queue
            } else {
                prevOrNext(1); // Play next track
            }
        });
    }


    private void sendMusicBroadcast() {
        sendMusicBroadcast(-1);
    }
    private void sendMusicBroadcast(int progress) {
        Intent intent = new Intent(Keys.MUSIC_STATE_CHANGED);
        intent.putExtra(Keys.EXTRA_STATE, musicState);
        if (progress > -1)
            intent.putExtra(Keys.EXTRA_PROGRESS, progress);
        sendBroadcast(intent);
    }

    private Notification createNotification() {
        String channelId = "music_service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "AM Play",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        MyAudioFile crrAudio = playing_queue.get(position);

        // Collapsed layout
        RemoteViews collapsed = new RemoteViews(getPackageName(), R.layout.custom_notification_collapsed);
        if (crrAudio != null) collapsed.setImageViewBitmap(R.id.album_art, crrAudio.getAlbumArt());
        collapsed.setTextViewText(R.id.txt_title, crrAudio != null ? crrAudio.getTitle() : "Unknown");
        collapsed.setTextViewText(R.id.txt_artist, crrAudio != null ? crrAudio.getArtists() : "Unknown");
        collapsed.setOnClickPendingIntent(R.id.btn_close, getPendingIntent(Keys.ACTION_STOP));

        // Expanded layout
        RemoteViews expanded = new RemoteViews(getPackageName(), R.layout.custom_notification_expanded);
        if (crrAudio != null) expanded.setImageViewBitmap(R.id.album_art, crrAudio.getAlbumArt());
        expanded.setTextViewText(R.id.txt_title, crrAudio != null ? crrAudio.getTitle() : "Unknown");
        expanded.setTextViewText(R.id.txt_artist, crrAudio != null ? crrAudio.getArtists() : "Unknown");

        expanded.setOnClickPendingIntent(R.id.btn_close, getPendingIntent(Keys.ACTION_STOP));
        expanded.setOnClickPendingIntent(R.id.btn_prev, getPendingIntent(Keys.ACTION_PREV));
        expanded.setOnClickPendingIntent(R.id.btn_next, getPendingIntent(Keys.ACTION_NEXT));
        expanded.setOnClickPendingIntent(R.id.btn_play_pause,
                getPendingIntent(musicState == Keys.STATE_PLAYING ? Keys.ACTION_PAUSE : Keys.ACTION_PLAY));
        expanded.setImageViewResource(R.id.btn_play_pause,
                musicState == Keys.STATE_PLAYING ? R.drawable.ic_pause_40 : R.drawable.ic_play_40);

        return new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.img_def_album_art)
                .setCustomContentView(collapsed)
                .setCustomBigContentView(expanded)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();
    }


    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(this, MusicService.class).setAction(action);
        return PendingIntent.getService(this, action.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
