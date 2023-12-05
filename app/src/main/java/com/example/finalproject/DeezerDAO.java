package com.example.finalproject;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) interface for interacting with the Deezer database.
 * Defines methods to perform CRUD (Create, Read, Update, Delete) operations on the Songs table.
 */
@Dao
public interface DeezerDAO {
    /**
     * Inserts a new song into the "Songs" table.
     *
     * @param song The Songs object to be inserted.
     * @return The row ID of the inserted song.
     */
    @Insert
    public long insertSong(Songs song);

    /**
     * Retrieves all songs from the "Songs" table.
     *
     * @return A list of Songs objects representing all songs in the database.
     */
    @Query("Select * from Songs")
    List<Songs> getAllSongs();
    /**
     * Deletes a song from the "Songs" table.
     *
     * @param song The Songs object to be deleted.
     */
    @Delete
    void deleteSongFromPlayList(Songs song);

    /**
     * Searches for songs with a specific title in the "Songs" table.
     *
     * @param title The title of the song to be searched.
     * @return A list of Songs objects matching the given title.
     */

    @Query("SELECT * FROM Songs WHERE title = :title")

    List<Songs> searchSong(String title);
}
