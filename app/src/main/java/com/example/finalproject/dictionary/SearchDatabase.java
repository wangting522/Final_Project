package com.example.finalproject.dictionary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//test
@Database(entities = {SearchWord.class}, version = 1)
public abstract class SearchDatabase extends RoomDatabase {
    public abstract SearchWordDAO swDAO();
}

