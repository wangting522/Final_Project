package com.example.finalproject.dictionary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * The Room database class for the application that encapsulates and abstracts the underlying SQLite database.
 * It serves as the main access point for the persisted data dealing with {@link SearchWord} entities.
 *
 * This class is annotated with {@code @Database}, indicating that it's a Room database and
 * should include the list of entities and the database version.
 */
@Database(entities = {SearchWord.class}, version = 1)
public abstract class SearchDatabase extends RoomDatabase {
    /**
     * Defines the database access object (DAO) for the {@link SearchWord} entity.
     * This method is abstract as Room will provide the implementation for you.
     *
     * @return The DAO implementation for accessing {@link SearchWord} entities.
     */
    public abstract SearchWordDAO swDAO();
}

