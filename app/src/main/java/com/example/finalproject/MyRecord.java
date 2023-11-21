package com.example.finalproject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MyRecord {
    @PrimaryKey (autoGenerate = true)
    @ColumnInfo(name="id")
    long id;
    @ColumnInfo(name="MessageColumn")
    String record;
    @ColumnInfo(name="TimeSentColumn")
    String timeSave;
    @ColumnInfo(name="SendReceiveColumn")
    boolean isSaveButton;
    @ColumnInfo(name = "latitude")
    String latitude;
    @ColumnInfo(name = "longitude")
    String longitude;
    public MyRecord() {}

    public MyRecord(String m, String t, boolean sent, String latitude, String longitude) {
        this.record = m;
        this.timeSave = t;
        this.isSaveButton = sent;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getTimeSave() {
        return timeSave;
    }

    public void setTimeSave(String timeSave) {
        this.timeSave = timeSave;
    }

    public boolean isSaveButton() {
        return isSaveButton;
    }

    public void setSaveButton(boolean saveButton) {
        isSaveButton = saveButton;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
