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
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.enums.Keys;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.MyUtils;

import static com.example.amplaybyalmamun.gadgets.utils.Store.playing_queue;

import java.util.Random;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private int musicState = Keys.STATE_STOPPED;
    private int currentIndex = 0;

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
        }

        startForeground(1, createNotification());
        return START_STICKY;
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            loadTrack(currentIndex);
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            musicState = Keys.STATE_PLAYING;
            sendMusicStateBroadcast(musicState);
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            musicState = Keys.STATE_PAUSED;
            sendMusicStateBroadcast(musicState);
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null && (mediaPlayer.isPlaying() || musicState == Keys.STATE_PAUSED)) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            musicState = Keys.STATE_STOPPED;
            sendMusicStateBroadcast(musicState);
            stopSelf();
        }
    }

    private void prevOrNext(int k) {
        //  k: 1 = next & -1 = prev
        currentIndex = AppSettings.shuffleStatus ?
                new Random().nextInt(playing_queue.size()) :
                (currentIndex + k) % playing_queue.size();
        restartTrack();
    }

    private void restartTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        loadTrack(currentIndex);
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

            boolean isLastTrack = currentIndex == playing_queue.size() - 1;
            if (AppSettings.repeatStatus == AppSettings.REPEAT_ONE) {
                restartTrack(); // Replay same track
            } else if (AppSettings.repeatStatus == AppSettings.REPEAT_ORDER && isLastTrack) {
                stopMusic(); // Stop at end of queue
            } else {
                prevOrNext(1); // Play next track
            }
        });
    }

    private void sendMusicStateBroadcast(int state) {
        Intent intent = new Intent(Keys.MUSIC_STATE_CHANGED);
        intent.putExtra("state", state);
        sendBroadcast(intent);
    }

    private Notification createNotification() {
        String channelId = "music_service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "AM Play",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }


        MyAudioFile crrAudio = playing_queue.get(currentIndex);
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle(crrAudio != null ? crrAudio.getTitle() : "Unknown")
                .setContentText(crrAudio != null ? crrAudio.getArtists() : "Unknown")
                .setSmallIcon(R.drawable.img_def_album_art)
                .addAction(R.drawable.ic_prev_40, "Prev", getPendingIntent(Keys.ACTION_PREV))
                .addAction(R.drawable.ic_play_40, "Play", getPendingIntent(Keys.ACTION_PLAY))
                .addAction(R.drawable.ic_pause_40, "Pause", getPendingIntent(Keys.ACTION_PAUSE))
                .addAction(R.drawable.ic_next_40, "Next", getPendingIntent(Keys.ACTION_NEXT))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(this, MusicService.class).setAction(action);
        return PendingIntent.getService(this, action.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private String getMusicStateText() {
        switch (musicState) {
            case Keys.STATE_PLAYING: return "Playing music...";
            case Keys.STATE_PAUSED: return "Music paused";
            case Keys.STATE_STOPPED: default: return "Music stopped";
        }
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
