package com.example.finalproject.dictionary;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
//test
import java.util.List;


@Dao
public interface SearchWordDAO {
    @Insert
    long insertSearchWord(SearchWord w);
    @Query("Select * from search_word;")
    List<SearchWord> getAllSearchWords();
    @Delete
    int deleteMessage(SearchWord w);
    // Check if the word is marked as favorite

}
