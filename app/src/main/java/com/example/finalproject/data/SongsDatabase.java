package com.example.finalproject.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Represents the Room Database for storing song-related data using the Room Persistence Library.
 */
@Database(entities = {Songs.class}, version = 1)
public abstract class SongsDatabase extends RoomDatabase {

    /**
     * Provides an instance of the Data Access Object (DAO) for interacting with the database.
     *
     * @return The DeezerDAO instance.
     */
    public abstract DeezerDAO deezerDao();
}
