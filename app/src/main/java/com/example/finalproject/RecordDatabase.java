package com.example.finalproject;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
/**
 * RecordDatabase is the main database class for the application.
 * It is built using the Room database library and serves as the primary access point
 * for the underlying SQLite database that stores MyRecord entities.
 *
 * This class is annotated as a RoomDatabase and declares the entities and the version of the database.
 */
@Database(entities = {MyRecord.class}, version = 1)
public abstract class RecordDatabase extends RoomDatabase {
    /**
     * Provides the DAO (Data Access Object) for MyRecord entities.
     *
     * @return Instance of MyRecordDAO for accessing MyRecord table operations.
     */
    public abstract MyRecordDAO myRecordDAO();

    private static volatile RecordDatabase INSTANCE;
    /**
     * Retrieves the singleton instance of the RecordDatabase.
     * This method builds the database instance using Room's database builder
     * to create a RoomDatabase object.
     *
     * The database is built as a singleton to prevent having multiple instances
     * of the database opened at the same time.
     *
     * @param context The application context.
     * @return The singleton instance of RecordDatabase.
     */
    public static RecordDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordDatabase.class, "FileOnTingPhone").build();
                }
            }
        }
        return INSTANCE;
    }
}

