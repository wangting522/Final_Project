package com.example.finalproject.data;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Locale;
/**
 * Represents a song entity with details such as ID, title, duration, album name,
 * album cover image, and artist name.
 */
@Entity
public class Songs {

    /**
     * The ID of the song stored in the database.
     */
    @PrimaryKey
    @ColumnInfo(name = "SongID")
    private long id;

    /**
     * Title of the song.
     */
    @ColumnInfo(name = "title")
    private String title;

    /**
     * Duration of the song in seconds.
     */
    @ColumnInfo(name = "duration")
    private int duration;

    /**
     * Album's name associated with the song.
     */
    @ColumnInfo(name = "albumName")
    private String albumName;

    /**
     * URL or identifier of the album's cover image.
     */
    @ColumnInfo(name = "albumCover")
    private String albumCover;

    /**
     * Artist's name associated with the song.
     */
    @ColumnInfo(name = "artistName")
    private String artistName;

    /**
     * Constructs a Songs object with specified attributes.
     *
     * @param id         The ID of the song.
     * @param title      The title of the song.
     * @param duration   The duration of the song in seconds.
     * @param albumName  The name of the album associated with the song.
     * @param albumCover The URL or identifier of the album's cover image.
     * @param artistName The name of the artist associated with the song.
     */
    public Songs(long id, String title, int duration, String albumName, String albumCover, String artistName) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.albumName = albumName;
        this.albumCover = albumCover;
        this.artistName = artistName;
    }

    /**
     * Gets the ID of the song.
     *
     * @return The song ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the song.
     *
     * @param id The new song ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the name of the album associated with the song.
     *
     * @return The album name.
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * Sets the name of the album associated with the song.
     *
     * @param albumName The new album name.
     */
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /**
     * Gets the URL or identifier of the album's cover image.
     *
     * @return The album cover URL or identifier.
     */
    public String getAlbumCover() {
        return albumCover;
    }

    /**
     * Sets the URL or identifier of the album's cover image.
     *
     * @param albumCover The new album cover URL or identifier.
     */
    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    /**
     * Gets the title of the song.
     *
     * @return The song title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the song.
     *
     * @param title The new song title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the duration of the song in seconds.
     *
     * @return The song duration in seconds.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the song in seconds.
     *
     * @param duration The new song duration in seconds.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Gets the duration of the song in MM:SS format.
     *
     * @return The song duration in MM:SS format.
     */
    public String getDurationInMMSS() {
        int minute = duration / 60;
        int second = duration % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minute, second);
    }

    /**
     * Gets the name of the artist associated with the song.
     *
     * @return The artist name.
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * Sets the name of the artist associated with the song.
     *
     * @param artistName The new artist name.
     */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}

