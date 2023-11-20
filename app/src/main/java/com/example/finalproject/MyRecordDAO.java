package com.example.finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyRecordDAO {
    @Insert
    public long insertRecord(MyRecord m);
    @Query("Select * from MyRecord")
    public List<MyRecord> getAllRecords();
    @Delete
    public int deleteRecord(MyRecord m);
}
