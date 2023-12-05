package com.example.finalproject.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalproject.data.MyRecord;

import java.util.List;
/**
 * Data Access Object (DAO) for the MyRecord entity.
 * This interface defines the methods for interacting with the MyRecord table in the database.
 * It provides functionality to insert, query, and delete records from the database.
 */
@Dao
public interface MyRecordDAO {
    /**
     * Inserts a MyRecord object into the database.
     *
     * @param m The MyRecord object to be inserted.
     * @return The row ID of the newly inserted record.
     */
    @Insert
    public long insertRecord(MyRecord m);
    /**
     * Retrieves all records from the MyRecord table.
     *
     * @return A list of MyRecord objects, representing all records in the table.
     */
    @Query("Select * from MyRecord")
    public List<MyRecord> getAllRecords();
    /**
     * Deletes a given MyRecord from the database.
     *
     * @param m The MyRecord object to be deleted.
     * @return The number of records deleted from the database.
     */
    @Delete
    public int deleteRecord(MyRecord m);
}
