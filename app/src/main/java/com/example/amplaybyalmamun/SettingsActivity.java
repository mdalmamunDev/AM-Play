package com.example.amplaybyalmamun;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.process.AppSettings;
import com.example.amplaybyalmamun.threads.Thread_ResetAllAmTags;

public class SettingsActivity extends AppCompatActivity {

    // views
    SeekBar sb_volume;
    SwitchCompat switch_enableNot, switch_shuffle;
    RadioGroup rg_repeat;
    RadioButton rb_repeatOne, rb_repeatAll, rb_repeatOrder;
    AppCompatButton btn_resetTags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // assign items
        assignItems();

        // settings
        AppSettings settings = new AppSettings(this);

        // shuffle
        switch_shuffle.setChecked(settings.getShuffleStatus());
        switch_shuffle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settings.setShuffleStatus(isChecked);
            if (isChecked) Toast.makeText(this, "Shuffle On", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Shuffle Off", Toast.LENGTH_SHORT).show();
        });

        /* System Volume */
        // AudioManager for controlling volume
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Set the max value of the SeekBar to the maximum system volume
        sb_volume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        // Set the initial progress of the SeekBar to the current system volume
        sb_volume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        // Set up an OnSeekBarChangeListener to respond to changes in volume
        sb_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Set the system volume based on the SeekBar progress
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
            }
        });
        /* System Volume end*/

        // repeat
            rb_repeatOne.setChecked(settings.getRepeatStatus() == AppSettings.REPEAT_ONE);
            rb_repeatAll.setChecked(settings.getRepeatStatus() == AppSettings.REPEAT_ALL);
            rb_repeatOrder.setChecked(settings.getRepeatStatus() == AppSettings.REPEAT_ORDER);

        rg_repeat.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_repeatOne) {
                settings.setRepeatStatus(AppSettings.REPEAT_ONE);
                Toast.makeText(this, "Repeat One", Toast.LENGTH_SHORT).show();
            }
            else if (checkedId == R.id.rb_repeatAll) {
                settings.setRepeatStatus(AppSettings.REPEAT_ALL);
                Toast.makeText(this, "Repeat All", Toast.LENGTH_SHORT).show();
            }
            if (checkedId == R.id.rb_repeatOrder) {
                settings.setRepeatStatus(AppSettings.REPEAT_ORDER);
                Toast.makeText(this, "Repeat Order", Toast.LENGTH_SHORT).show();
            }
        });

        // reset am tags
        findViewById(R.id.btn_resetTags).setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.show();

            Thread_ResetAllAmTags thread = new Thread_ResetAllAmTags(this, dialog);
            thread.start();
        });


        // back
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }

    // override on back pressed method
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void assignItems() {
        switch_enableNot = findViewById(R.id.switch_enableNot);
        switch_shuffle = findViewById(R.id.switch_shuffle);
        sb_volume = findViewById(R.id.seekbar_volume);
        rg_repeat = findViewById(R.id.rg_repeat);
        rb_repeatOne = findViewById(R.id.rb_repeatOne);
        rb_repeatAll = findViewById(R.id.rb_repeatAll);
        rb_repeatOrder = findViewById(R.id.rb_repeatOrder);
        btn_resetTags = findViewById(R.id.btn_resetTags);
    }
}