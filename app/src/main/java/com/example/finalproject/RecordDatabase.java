package com.example.finalproject;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MyRecord.class}, version = 1)
public abstract class RecordDatabase extends RoomDatabase {
    public abstract MyRecordDAO myRecordDAO();

    private static volatile RecordDatabase INSTANCE;

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

