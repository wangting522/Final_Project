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
    @Query("SELECT EXISTS(SELECT 1 FROM search_word WHERE word = :word AND isFavorite = 1)")
    boolean isWordFavorite(String word);

    // Mark the word as favorite in the search_word table
    @Query("UPDATE search_word SET isFavorite = 1 WHERE word = :word")
    void insertFavoriteWord(String word);

}
