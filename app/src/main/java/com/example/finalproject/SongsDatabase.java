package com.example.finalproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Songs.class},version = 1)
public abstract class SongsDatabase extends RoomDatabase {
    public abstract DeezerDAO deezerDao();
}
