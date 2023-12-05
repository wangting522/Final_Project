package com.example.finalproject.data;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
//test
import com.example.finalproject.data.SearchWord;

import java.util.List;

/**
 * Interface representing the Data Access Object (DAO) for the SearchWord entity.
 * <p>
 * This interface defines the methods for interacting with the SearchWord table in the database.
 * It includes operations for inserting new entries, retrieving all entries, and deleting entries.
 * Room library processes these annotations to generate the necessary code for these operations.
 */
@Dao
public interface SearchWordDAO {
    /**
     * Inserts a new SearchWord entry into the database.
     * <p>
     * This method adds a new SearchWord object to the database. It returns the primary key (ID)
     * of the newly inserted row.
     *
     * @param w The SearchWord object to be inserted into the database.
     * @return The ID of the newly inserted SearchWord entry.
     */
    @Insert
    long insertSearchWord(SearchWord w);

    /**
     * Retrieves all SearchWord entries from the database.
     * <p>
     * This method returns a list of all SearchWord objects currently stored in the database.
     * It is useful for displaying all dictionary entries or performing operations on them.
     *
     * @return A list of all SearchWord entries in the database.
     */
    @Query("Select * from search_word;")
    List<SearchWord> getAllSearchWords();

    /**
     * Deletes a specific SearchWord entry from the database.
     * <p>
     * This method removes the given SearchWord object from the database. It returns the number
     * of rows affected by the operation, which should be 1 in case of a successful deletion.
     *
     * @param w The SearchWord object to be deleted from the database.
     * @return The number of rows affected by the delete operation.
     */
    @Delete
    int deleteMessage(SearchWord w);
    // Check if the word is marked as favorite

}
