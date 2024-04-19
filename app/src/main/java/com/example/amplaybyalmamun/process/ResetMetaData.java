package com.example.amplaybyalmamun.process;

import static com.example.amplaybyalmamun.gadgets.utils.MyUtils.toList;

import com.example.amplaybyalmamun.gadgets.models.MyAudioFile;
import com.example.amplaybyalmamun.gadgets.utils.Store;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetMetaData {
    private TextInputEditText et_title, et_album, et_artist, et_albumArtist, et_genre, et_tags, et_year, et_date, et_track, et_name;
    MyAudioFile file;

    public ResetMetaData(MyAudioFile file) {
        this.file = file;
    }

    /* setter and getter */
    // title
    public void setEt_title(TextInputEditText et_title) {
        this.et_title = et_title;
    }
    public TextInputEditText getEt_title() {
        return et_title;
    }

    // album
    public void setEt_album(TextInputEditText et_album) {
        this.et_album = et_album;
    }
    public TextInputEditText getEt_album() {
        return et_album;
    }

    // artist
    public void setEt_artist(TextInputEditText et_artist) {
        this.et_artist = et_artist;
    }
    public TextInputEditText getEt_artist() {
        return et_artist;
    }


    // albumArtist
    public void setEt_albumArtist(TextInputEditText et_albumArtist) {
        this.et_albumArtist = et_albumArtist;
    }
    public TextInputEditText getEt_albumArtist() {
        return et_albumArtist;
    }

    // genre
    public void setEt_genre(TextInputEditText et_genre) {
        this.et_genre = et_genre;
    }
    public TextInputEditText getEt_genre() {
        return et_genre;
    }

    // tags
    public void setEt_tags(TextInputEditText et_tags) {
        this.et_tags = et_tags;
    }
    public TextInputEditText getEt_tags() {
        return et_tags;
    }

    // year
    public void setEt_year(TextInputEditText et_year) {
        this.et_year = et_year;
    }
    public TextInputEditText getEt_year() {
        return et_year;
    }

    // date
    public void setEt_date(TextInputEditText et_date) {
        this.et_date = et_date;
    }
    public TextInputEditText getEt_date() {
        return et_date;
    }

    // et_track
    public void setEt_track(TextInputEditText et_track) {
        this.et_track = et_track;
    }
    public TextInputEditText getEt_track() {
        return et_track;
    }

    // et_name
    public void setEt_name(TextInputEditText et_name) {
        this.et_name = et_name;
    }
    public TextInputEditText getEt_name() {
        return et_name;
    }
    /* setter and getter ends */

    // get text from et
    private String getTextFromEt(TextInputEditText et) {
        if (et == null) return "";
        return Objects.requireNonNull(et.getText()).toString().trim();
    }
    private String resetComas(String value) {
        StringBuilder sb = new StringBuilder();
        List<String> list = toList(value, ",");
        for (int i=0; i<list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size()-1) sb.append(", ");
        }

        return sb.toString().trim();
    }
    private String extractDataFrom(String text, String regEx) { // regEx example = "\\(", "\\)"
        String str = "";
        // Define a regular expression pattern to match the date format "(yyyy-mm-dd)"
        Pattern pattern = Pattern.compile(regEx);

        // Create a Matcher object to find the pattern in the text
        Matcher matcher = pattern.matcher(text);

        // Check if the pattern is found
        if (matcher.find()) // Extract and return the matched date
            str = matcher.group(1);

        return str;
    }



    // title
    public String title() {
        // Song's First Part - Album(2002-08-10) - Artist
        String title = getTextFromEt(getEt_title()); // get from input field
        if (title.isEmpty()) title = getTextFromEt(getEt_name()); // get from file name
        if (title.isEmpty()) return "";

        // first part
        List<String> list = toList(title, "-");
        String first = (list.size() > 0) ? list.get(0) : "";
        // middle/album & date part
        List<String> albumList = toList(getTextFromEt(getEt_album()), "\\("); // get from album
        String album = (albumList.size() > 0) ? albumList.get(0) : "";
        String year = getTextFromEt(getEt_year()); // get from year
        String date = getTextFromEt(getEt_date()); // get from date
        // last/artist part
        String artist = getTextFromEt(getEt_artist()); // get from artist

        // prepare full title
        title = "";
        if (!first.isEmpty()) title += first.trim();
        if (!album.isEmpty()) title += " - " + album;
        if (!year.isEmpty() || !date.isEmpty()) {
            if (date.isEmpty()) title += " ";
            title += "(";
            if (!year.isEmpty()) title += year;
            if (!date.isEmpty()) title +=  "-" + date;
            title += ")";
        }
        if (!artist.isEmpty()) title += " - " + artist;

        return title.trim();
    }
    private String extractAlbumFromTitle() {
        List<String> list = toList(getTextFromEt(getEt_title()), "-");
        String albumArea = "";
        if (list.size() > 1) albumArea = list.get(1);

        String album = "";
        List<String> listAlbumArea = toList(albumArea, "\\(");
        if (listAlbumArea.size() > 0) album = listAlbumArea.get(0);

        return album.trim();
    }
    private String extractArtistFromTitle() { // will not take from less then < 1
        String artist = "";

        List<String> list = toList(getTextFromEt(getEt_title()), "-");
        if (list.size() > 1) artist = list.get(list.size()-1);

        return artist.trim();
    }
    private String extractYearFromTitle() {
        String data = extractDataFrom(getTextFromEt(getEt_title()), "\\((\\d{4})\\)");  // regEx example = "\\(", "\\)"
        List<String> list = toList(data, "-");
        if (list.size() > 0) return list.get(0);
        return "";
    }

    // album
    public String album() {
        String album = getTextFromEt(getEt_album());          // get from input field
        if (album.isEmpty()) album = extractAlbumFromTitle(); // get from title

        String year = extractDataFrom(album, "\\((\\d{4})\\)");
        if (year.isEmpty()) {// check is year present
            String y = getTextFromEt(getEt_year());          // get year from year
            if (y.isEmpty()) y = extractYearFromTitle();// get year from title
            if (!y.isEmpty()) album += " (" + y + ")";
        }

        return album.trim();
    }
    private String extractYearFromAlbum() {
        return extractDataFrom(getTextFromEt(getEt_album()), "\\((\\d{4})\\)");
    }

    // artist
    public String artist() {
        String artist = getTextFromEt(getEt_artist()); // get from input field
        if (artist.isEmpty()) artist = getTextFromEt(getEt_albumArtist()); // get from albumArtist
        if (artist.isEmpty()) artist = extractArtistFromTitle();// get from title

        artist = resetComas(artist);
        return artist;
    }

    // album artis
    public String albumArtist() {

        // get albumArtist from artist
        String artists = getTextFromEt(getEt_albumArtist()); // get from self
        if (artists.isEmpty()) artists = getTextFromEt(getEt_artist()); // get from artist
        if (artists.isEmpty()) artists = extractArtistFromTitle(); // get from title

        return resetComas(artists);
    }

    // genre
    public String genre() {

        String genre = getTextFromEt(getEt_genre());

        // get genre first
        String genreFirst = file.getGenreFirst(genre);
        // get main tag
        List<String> list = toList(getTextFromEt(getEt_tags()), ",");
        String tag = (list.size() > 0) ? list.get(0) : ""; // get main tag from tags
        if (tag.isEmpty()) tag = file.getMainAmTag(genre); // get main tag from self
        if (tag.isEmpty()) return genreFirst;

        // prepare new genre
        genre = genreFirst + " (";
        if (tag.equalsIgnoreCase(Store.mainAmTags[Store.mainTagBroken]))
            genre += "Heart ";
        genre += tag + " Songs)";

        return genre;
    }

    // tags
    public String tags() {

        String tags = getTextFromEt(getEt_tags());
        String mainTag = file.getMainAmTag(getTextFromEt(getEt_genre()));
        if (!mainTag.isEmpty()) {
            StringBuilder sb = new StringBuilder(mainTag);
            List<String> list = toList(tags, ",");
            for (String s : list) {
                if (!s.equalsIgnoreCase(mainTag)) {
                    sb.append(", ").append(s);
                }
            }
            tags = sb.toString();
        }

        return resetComas(tags);
    }

    // year
    public String year() {
        String year = getTextFromEt(getEt_year()); // get from self
        if (year.isEmpty()) year = extractYearFromTitle(); // get from title
        if (year.isEmpty()) year = extractYearFromAlbum(); // get from album

        return year.trim();
    }


    // date
    public String date() {
        // get date
        String mm = "", dd = "";
        String date = getTextFromEt(getEt_date()); // get from self
        if (date.isEmpty()) { // get from title
            String data = extractDataFrom(getTextFromEt(et_title), "\\((\\d{4}-\\d{2}-\\d{2})\\)");  // regEx example = "\\(", "\\)"
            List<String> dates = toList(data, "-");
            if (dates.size() > 2) {
                mm = dates.get(1);
                dd = dates.get(2);
            }
        }
        if (date.isEmpty() && mm.isEmpty() && dd.isEmpty()) return "";

        /* format */
        if (!date.isEmpty()) {
            // remove unnecessary
            StringBuilder sb = new StringBuilder();
            for (char c : date.toCharArray())
                if (Character.isDigit(c) || c == '-') sb.append(c);
            date = sb.toString();
            if (date.isEmpty()) return "";

            // separate mm/dd
            String[] arr = date.split("-");
            mm = arr[0];
            dd = (arr.length > 1) ? arr[1] : "";
        }

        // range check
        if (!mm.isEmpty()) {
            int M = Integer.parseInt(mm);
            if (M > 12 || M < 1)
                mm = (M > 12) ? "12" : "01";
        }
        if (!dd.isEmpty()) {
            int D = Integer.parseInt(dd);
            String year =  getTextFromEt(getEt_year());
            int y = (!year.isEmpty()) ? Integer.parseInt(year) : 2002;
            int m = (!mm.isEmpty()) ? Integer.parseInt(mm) : 1;
            int lastLim = getLastDayOfMonth(y, m);
            if (D > lastLim || D < 1)
                dd = (D > lastLim) ? lastLim+"" : "01";
        }
         /* format ends */

        // update
        date = mm + "-" + dd;
        return date;
    }
    private int getLastDayOfMonth(int year, int month) {
        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();

        // Set the year and month
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Month is zero-based (0 - January, 11 - December)

        // Set the day to the last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        // Get and return the day
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // track
    public String track() {
        String track = getTextFromEt(getEt_track());
        StringBuilder sb = new StringBuilder();

        for(char c : track.toCharArray())
            if (Character.isDigit(c)) sb.append(c);

        track = sb.toString();
        return track;
    }


    // name
    public String name() {
        return  getTextFromEt(et_title);
    }
}
