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

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private int musicState = Keys.STATE_STOPPED; // Default state is stopped

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        // Load music if not yet initialized
        if (mediaPlayer == null && intent.hasExtra(Keys.EXTRA_PATH)) {
            String path = intent.getStringExtra(Keys.EXTRA_PATH); // default fallback
            mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
            mediaPlayer.setLooping(true);
        }

        if (Keys.ACTION_PLAY.equals(action)) {
            playMusic();
        } else if (Keys.ACTION_PAUSE.equals(action)) {
            pauseMusic();
        } else if (Keys.ACTION_STOP.equals(action)) {
            stopMusic();
        }

        startForeground(1, createNotification());

        return START_STICKY;
    }

    private void playMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            musicState = Keys.STATE_PLAYING;
            sendMusicStateBroadcast(musicState); // Notify state change
        }
    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            musicState = Keys.STATE_PAUSED;
            sendMusicStateBroadcast(musicState); // Notify state change
        }
    }

    private void stopMusic() {
        if (mediaPlayer.isPlaying() || musicState == Keys.STATE_PAUSED) {
            mediaPlayer.stop();
            musicState = Keys.STATE_STOPPED;
            sendMusicStateBroadcast(musicState); // Notify state change
            stopSelf();
        }
    }

    private void sendMusicStateBroadcast(int state) {
        Intent intent = new Intent(Keys.MUSIC_STATE_CHANGED);
        intent.putExtra("state", state);
        sendBroadcast(intent); // Send the broadcast
    }

    private Notification createNotification() {
        String channelId = "music_service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Music Player",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        PendingIntent playIntent = PendingIntent.getService(this, 0,
                new Intent(this, MusicService.class).setAction(Keys.ACTION_PLAY),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pauseIntent = PendingIntent.getService(this, 0,
                new Intent(this, MusicService.class).setAction(Keys.ACTION_PAUSE),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent stopIntent = PendingIntent.getService(this, 0,
                new Intent(this, MusicService.class).setAction(Keys.ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Music Player")
                .setContentText(getMusicStateText()) // Display current state in notification
                .setSmallIcon(R.drawable.ic_play_40)
                .addAction(R.drawable.ic_play_40, "Play", playIntent)
                .addAction(R.drawable.ic_pause_40, "Pause", pauseIntent)
                .addAction(R.drawable.ic_pause_40, "Stop", stopIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private String getMusicStateText() {
        switch (musicState) {
            case Keys.STATE_PLAYING:
                return "Playing music...";
            case Keys.STATE_PAUSED:
                return "Music paused";
            case Keys.STATE_STOPPED:
            default:
                return "Music stopped";
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
