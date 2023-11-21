package com.example.finalproject;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordListDAO {

    @Insert //id                 //@Entity
    public long insertWord(WordList m); //for insertion, long is the new id

    @Query("Select * from WordList;")  //the SQL search
    public List<WordList> getAllMessages();  //for query

    @Delete //number of rows deleted//a
    public int deleteThisMessage(WordList m);  //delete this message by id
}
