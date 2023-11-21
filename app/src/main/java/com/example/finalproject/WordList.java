package com.example.finalproject;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity //mapping variables to columns
public class WordList {

    @PrimaryKey(autoGenerate = true)  //the database will increment them for us
    @ColumnInfo(name="id")
    long id;

    @ColumnInfo(name="WordColumn")
    String word;

    @ColumnInfo(name="TimeSentColumn")
    String timeSent;

    @ColumnInfo(name="SendReceivedColumn")
    boolean sentOrReceive;

    public WordList(String word, String timeSent, boolean sentOrReceive) {
        this.word = word;
        this.timeSent = timeSent;
        this.sentOrReceive = sentOrReceive;
    }

    public String getMessage() {
        return word;
    }//a

    public String getTimeSent() {
        return timeSent;
    }

    public boolean isSentButton() {
        return sentOrReceive;
    }
}