package com.example.finalproject.data;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
/**
 * The DeezerAlbum class represents an album from Deezer with its attributes like ID, title, artist name,
 * cover URL, release date, and label.
 */

public class DeezerAlbum {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    private long albumId;

    @ColumnInfo(name = "albumTitle")
    private String title;

    @ColumnInfo(name = "artistName")
    private String artistName;

    @ColumnInfo(name = "coverURL")
    private String coverUrl;



    private String albumRelease;

    private String albumLabel;

    /**
     * Constructs a DeezerAlbum with the specified attributes.
     *
     * @param albumId    The unique ID of the album.
     * @param title      The title of the album.
     * @param artistName The name of the artist associated with the album.
     * @param coverUrl   The URL of the album cover.
     */
    public DeezerAlbum(long albumId, String title, String artistName, String coverUrl) {
        this.albumId = albumId;
        this.title = title;
        this.artistName = artistName;
        this.coverUrl = coverUrl;
    }

    /**
     * Gets the unique ID of the album.
     *
     * @return The album ID.
     */
    public long getAlbumId() {
        return albumId;
    }

    /**
     * Gets the title of the album.
     *
     * @return The album title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the name of the artist associated with the album.
     *
     * @return The artist name.
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * Gets the URL of the album cover.
     *
     * @return The album cover URL.
     */
    public String getCoverUrl() {
        return coverUrl;
    }

    /**
     * Gets the release date of the album.
     *
     * @return The album release date.
     */
    public String getAlbumRelease() {
        return albumRelease;
    }

    /**
     * Sets the release date of the album.
     *
     * @param albumRelease The album release date.
     */
    public void setAlbumRelease(String albumRelease) {
        this.albumRelease = albumRelease;
    }

    /**
     * Gets the label of the album.
     *
     * @return The album label.
     */
    public String getAlbumLabel() {
        return albumLabel;
    }

    /**
     * Sets the label of the album.
     *
     * @param albumLabel The album label.
     */
    public void setAlbumLabel(String albumLabel) {
        this.albumLabel = albumLabel;
    }
}

