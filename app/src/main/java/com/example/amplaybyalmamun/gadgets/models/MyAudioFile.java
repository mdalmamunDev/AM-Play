package com.example.amplaybyalmamun.gadgets.models;

import static com.example.amplaybyalmamun.gadgets.utils.Store.mainAmTags;
import static com.example.amplaybyalmamun.gadgets.utils.Store.mainTagBroken;
import static com.example.amplaybyalmamun.gadgets.utils.Store.mainTagHeat;
import static com.example.amplaybyalmamun.gadgets.utils.Store.mainTagLove;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.example.amplaybyalmamun.gadgets.utils.MyUtils;
import com.example.amplaybyalmamun.process.DB_Helper;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MyAudioFile {
    private int idDB = -1; // db id
    private long idFile = -1;
    private String filePath = "";
    private String title = "";
    private String album = "";
    private String artists = "";
    private String albumArtist = "";
    private String genre = "";
    private String year = "";
    private String date = ""; // custom
    private String track = "";
    private String amTags = ""; // custom
    private long duration = -1;
    private long size = -1;
    private int bitRate = -1;
    private String mimeType = "";
    private boolean isPlaying = false;
    private boolean isFavorite = false;
    private boolean isSet = false;
    public boolean isSetPrimary = false;


    // constructors
    public MyAudioFile() {
        //
    }
    public void copyTo(MyAudioFile other) {
        if (other == null) return;

        this.idDB = (other.idDB > -1) ? other.idDB : this.idDB;
        this.idFile = (other.idFile > -1) ? other.idFile : this.idFile;
        this.filePath = (!other.filePath.isEmpty()) ? other.filePath : this.filePath;
        this.title = (!other.title.isEmpty()) ? other.title : this.title;
        this.album = (!other.album.isEmpty()) ? other.album : this.album;
        this.artists = (!other.artists.isEmpty()) ? other.artists : this.artists;
        this.albumArtist = (!other.albumArtist.isEmpty()) ? other.albumArtist : this.albumArtist;
        this.genre = (!other.genre.isEmpty()) ? other.genre : this.genre;
        this.year = (!other.year.isEmpty()) ? other.year : this.year;
        this.date = (!other.date.isEmpty()) ? other.date : this.date;
        this.track = (!other.track.isEmpty()) ? other.track : this.track;
        this.amTags = (!other.amTags.isEmpty()) ? other.amTags : this.amTags;
        this.duration = (other.duration > -1) ? other.duration : this.duration;
        this.size = (other.size > -1) ? other.size : this.size;
        this.bitRate = (other.bitRate > -1) ? other.bitRate : this.bitRate;
        this.mimeType = (!other.mimeType.isEmpty()) ? other.mimeType : this.mimeType;
        this.isPlaying = other.isPlaying;
        this.isFavorite = other.isFavorite;
    }


    // db id
    public void setIdDB(int idDB) {
        this.idDB = idDB;
    }
    public int getIdDB() {
        return idDB;
    }

    // file id
    public long getIdFile() {
        return idFile;
    }
    public void setIdFile(long idFile) {
        this.idFile = idFile;
    }

    // path
    public void setPath(String filePath) {
        this.filePath = filePath;
    }
    public String getPath() {
        return filePath;
    }

    // dir
    public String getDir() {
        if(getPath().isEmpty()) return "";

        String[] arr = getPath().split("/");

        if(arr.length < 2) return "0";
        return arr[arr.length-2].trim();
    }
    public String getDirPath() {
        if (exists()) return new File(getPath()).getParent();
        return getDirPath(getPath());
    }
    public String getDirPath(String path) {
        if (path.isEmpty()) return "";

        List<String> list = toList(path, "/");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<list.size()-1; i++) {
            sb.append(list.get(i));
            if (i != list.size()-2) sb.append("/");
        }
        return sb.toString();
    }

    // title
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    // file name
    public String getName() {
        if (getPath().isEmpty()) return "";

        String name = new File(getPath()).getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    // album
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getAlbum() {
        return album;
    }

    // artists
    public void setArtists(String artists) {
        this.artists = artists;
    }
    public String getArtists() {
        return artists;
    }
    public List<String> getArtistList() {
        return toList(artists);
    }

    // album artist
    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }
    public String getAlbumArtist() {
        return albumArtist;
    }

    // genre
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getGenre() {
        return genre;
    }
    public String getGenreFirst() {
        return getGenreFirst(getGenre());
    }

    public String getGenreFirst(String genre) {
        if (genre == null || genre.equals("")) return "";
        String[] arr = genre.split(" ");
        if (arr.length > 1 && arr[1].equalsIgnoreCase("pop")) {
            return arr[0]+" "+arr[1];
        }
        return arr[0];
    }

    // year
    public void setYear(String year) {
        this.year = year;
    }
    public String getYear() {
        return year;
    }

    // date
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }

    // track
    public void setTrack(String track) {
        this.track = track;
    }
    public String getTrack() {
        return track;
    }

    // am tags
    public void setAmTags(String amTags) {
        this.amTags = amTags;
    }
    public String getAmTags() {
        return amTags;
    }
    public List<String> getTagsList() {
        return toList(getAmTags());
    }
    public String getMainAmTag() {
        return getMainAmTag(getGenre());
    }
    public String getMainAmTag(String genre) {
        if (genre.toLowerCase().contains( mainAmTags[mainTagLove].toLowerCase() ))
            return mainAmTags[mainTagLove];
        if (genre.toLowerCase().contains( mainAmTags[mainTagBroken].toLowerCase() ))
            return mainAmTags[mainTagBroken];
        if (genre.toLowerCase().contains( mainAmTags[mainTagHeat].toLowerCase() ))
            return mainAmTags[mainTagHeat];
        return "";
    }

    // duration
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public long getDuration() {
        return duration;
    }
    public String getDurationInTime() {
        return MyUtils.getDurationFormatted(getDuration());
    }

    // size
    public void setSize(long size) {
        this.size = size;
    }
    public long getSize() {
        return size;
    }


    // bit rate
    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }
    public int getBitRate() {
        return bitRate;
    }

    // mime type
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    public String getMimeType() {
        return mimeType;
    }

    // album art
    public  Bitmap getAlbumArt() {
        Tag tags = getMD();
        if (tags != null && tags.getFirstArtwork() != null) {
            byte[] imageData = tags.getFirstArtwork().getBinaryData();
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        return null;
    }

    // playing status
    public void setPlaying(boolean val) {
        isPlaying = val;
    }
    public boolean isPlaying() {
        return isPlaying;
    }


    // favorite status
    public void setFavorite(boolean val) {
        isFavorite = val;
    }
    public boolean isFavorite() {
        return isFavorite;
    }


    // get file object
    public File getFile() {
        return new File(getPath());
    }

    // unique id
    public String getUniqueId() {
        return filePath + size + duration;
    }

    // file search info
    public String getFileInfo() {
        return getTitle() +" "+ getAlbum() +" "+ getArtists() +" "+ getAlbumArtist() +" "+ getDate();
    }


    // exists
    public boolean exists() {
        return new File(filePath).exists();
    }



    // prepare file
    public void prepare(Context context) {
        prepare(context, true);
    }
    public void prepare(Context context, boolean isAll) {
        /* get all tags using JAudioTagger */
        Tag tags = getMD();
        if ((isSet && !isAll) || tags == null) return;

        // Print the metadata from the tag
        title           =  tags.getFirst(FieldKey.TITLE);
        album           =  tags.getFirst(FieldKey.ALBUM);
        artists         =  tags.getFirst(FieldKey.ARTIST);
        albumArtist     =  tags.getFirst(FieldKey.ALBUM_ARTIST);
        genre           =  tags.getFirst(FieldKey.GENRE);
        amTags          =  tags.getFirst(FieldKey.TAGS);
        year            =  tags.getFirst(FieldKey.YEAR);
        date            =  tags.getFirst(FieldKey.ISRC);
        track           =  tags.getFirst(FieldKey.TRACK);

        if (isAll) {
            // set isPlaying
            MyAudioFile lastPlayedFile = MyUtils.getLastPlayedFile(context);
            if (lastPlayedFile != null && lastPlayedFile.getUniqueId().equals(getUniqueId()))
                setPlaying(true);
            // set isFavorite
            DB_Helper dbHelper = new DB_Helper(context);/*MyUtils.isPresent(dbHelper.getAllAudioItems(DB_Helper.TABLE_FAVORITES), this);*/
            setFavorite(dbHelper.getAudioItem(DB_Helper.TABLE_FAVORITES, this) != null);
            dbHelper.close();
        }
        // update
        isSet = true;
    }

    private Tag getMD() {
        if (!exists() || !isAudio()) return null;
        Tag tags;
        try {
            AudioFile audio = AudioFileIO.read(new File(getPath()));
            tags = audio.getTag();
        } catch (CannotReadException | TagException | InvalidAudioFrameException |
                 ReadOnlyFileException | IOException e) {
            throw new RuntimeException(e);
        }

        return tags;
    }

    // str elements to list. example "Love, Broken, Rain" => List[Love, Broken, Rain]
    public List<String> toList(String str) {
        return toList(str, ",");
    }
    public List<String> toList(String str, String regEx) {
        return MyUtils.toList(str, regEx);
    }




    public boolean isAudio() {
        if (!exists()) return false;
        String fileExtension = getPath().substring(getPath().lastIndexOf('.') + 1);
        // Skip processing if it's not a supported audio file
        return isSupportedAudioFileExtension(fileExtension);
    }
    // Helper method to check if the file extension is supported
    private boolean isSupportedAudioFileExtension(String fileExtension) {
        // Add more supported audio file extensions if needed
        return fileExtension.equalsIgnoreCase("mp3") ||
                fileExtension.equalsIgnoreCase("flac") ||
                fileExtension.equalsIgnoreCase("wav");
    }
}
