package com.example.finalproject.dictionary;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "search_word")
public class SearchWord {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    long id;
    @ColumnInfo(name="WordColumn")
    protected String word;

    @ColumnInfo(name="TimeSentColumn")
    protected String timeSent;

    public  SearchWord(){}

    public SearchWord(String word, String timeSent) {
        this.word = word;
        this.timeSent = timeSent;
    }


    public String getWord() {
        return word;
    }
    public String getTimeSent() {
        return timeSent;
    }
}
