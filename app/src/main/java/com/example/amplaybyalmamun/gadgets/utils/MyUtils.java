package com.example.amplaybyalmamun.gadgets.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.example.amplaybyalmamun.R;
import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.process.DB_Helper;

import org.jaudiotagger.audio.generic.Utils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class MyUtils {

    // rename file
    public static boolean rename(File fromFile, File toFile) {
        return Utils.rename(fromFile, toFile);
    }

    // Method to notify the media scanner about a file or directory change
    public static void notifyMediaScanner(Context context, String filePath) {
        // Pass the file path to the media scanner
        MediaScannerConnection.scanFile(context,
                new String[]{filePath},  // Array of paths to scan
                null,  // MIME type
                (path, uri) -> {
                    // Scan completed callback, you can handle any post-scan actions here
                });

    }
/* files  ends */



/* database */
    public static int getLatPlayedIdx(Context context, List<MyAudioFile> list) {
        MyAudioFile file = getLastPlayedFile(context);
        return getIndex(list, file);
    }
    public static MyAudioFile getLastPlayedFile(Context context) {
        DB_Helper dbHelper = new DB_Helper(context);
        MyAudioFile file = dbHelper.getLastAddedAudioItem(DB_Helper.TABLE_AUDIO_HISTORY);
        dbHelper.close();
        return file;
    }

    // search from list and return the index
    public static int getIndex(List<MyAudioFile> list, MyAudioFile file) {
        if (list == null || file == null) return -1;

        for (int i=0; i<list.size(); i++) {
            MyAudioFile crrFile = list.get(i);
            if (crrFile == null) continue;
            if (crrFile.getUniqueId().equals(file.getUniqueId())) {
                return i;
            }
        }
        return -1;
    }
/* database ends*/

/*  UI  */
    // Animation
    public static void loadAnimation(View view, int animRes) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), animRes);
        view.startAnimation(animation);
    }
    public static void setOnClickAnim(View btn) {
        // Apply a scale animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(150);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);

        btn.startAnimation(scaleAnimation);
        btn.setBackgroundResource(R.color.trans_dark_x); // Set your desired color
        new Handler().postDelayed(() -> {
            btn.setBackgroundResource(R.color.trans); // Reset to transparent or your original background color
        }, 150); // Change the delay as needed
    }
/*  UI Ends */

/* Others gadgets */
    public static int random(int range) {
        return random(0,range);
    }
    public static int random(int start, int end) {
        return (int) (Math.random() * (end - start));
    }
    public static List<String> toList(String str, String regEx) {
        if (str == null || str.isEmpty()) return new LinkedList<>();
        String[] tagArray = str.split(regEx);
        List<String> list = new ArrayList<>();

        for (String tag : tagArray) list.add(tag.trim());

        return list;
    }
    public static String getDurationFormatted(long duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = duration / (1000 * 60 * 60);

        // Format the hours, minutes, and seconds as strings
        String hoursStr = String.format(Locale.US, "%02d", hours);
        String minutesStr = String.format(Locale.US, "%02d", minutes);
        String secondsStr = String.format(Locale.US, "%02d", seconds);

        if (hours > 0) {
            return hoursStr + ":" + minutesStr + ":" + secondsStr;
        } else {
            return minutesStr + ":" + secondsStr;
        }
    }

    public static String getTime() {
        LocalDate date = null; LocalTime time = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.now();
            time = LocalTime.now();
        }

        return date + "_" + time;
    }

    public static Set<String> sortSet(Set<String> set) {
        return new TreeSet<>(set); // Convert HashSet to TreeSet for sorting
    }
    public static Set<String> sortSetDescending(Set<String> set) {
        Comparator<String> descendingComparator = Collections.reverseOrder();
        TreeSet<String> sortedSet = new TreeSet<>(descendingComparator);
        sortedSet.addAll(set);
        return sortedSet;
    }
    public static void showProblem(Context context) {
        Toast.makeText(context, "Something is wrong", Toast.LENGTH_SHORT).show();
    }
/* Others gadgets ends */
}
