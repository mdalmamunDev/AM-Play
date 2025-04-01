package com.example.amplaybyalmamun.process;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.amplaybyalmamun.R;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_STOP = "STOP";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music); // Load your audio file
        mediaPlayer.setLooping(true); // Loop the music
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_PLAY.equals(action)) {
            playMusic();
        } else if (ACTION_PAUSE.equals(action)) {
            pauseMusic();
        } else if (ACTION_STOP.equals(action)) {
            stopSelf();
        }

        // Start the service as a foreground service
        startForeground(1, createNotification());

        return START_STICKY;
    }

    private void playMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private Notification createNotification() {
        String channelId = "music_service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Music Player",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        PendingIntent playIntent = PendingIntent.getService(this, 0,
                new Intent(this, MusicService.class).setAction(ACTION_PLAY),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pauseIntent = PendingIntent.getService(this, 0,
                new Intent(this, MusicService.class).setAction(ACTION_PAUSE),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent stopIntent = PendingIntent.getService(this, 0,
                new Intent(this, MusicService.class).setAction(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Music Player")
                .setContentText("Playing music...")
                .setSmallIcon(R.drawable.ic_play_40)
                .addAction(R.drawable.ic_play_40, "Play", playIntent)
                .addAction(R.drawable.ic_pause_40, "Pause", pauseIntent)
                .addAction(R.drawable.ic_pause_40, "Stop", stopIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
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

