package com.example.finalproject.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
/**
 * Entity class representing a record in the application.
 * This class is used to store information about a particular record, including its message, timestamp,
 * and geographical coordinates. It is used with Room for data persistence.
 */
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
    /**
     * Default constructor for MyRecord.
     */
    public MyRecord() {}
    /**
     * Constructor for MyRecord with parameters.
     * @param m The message or content of the record.
     * @param t The timestamp when the record was saved or received.
     * @param sent Indicates whether the record is saved or not.
     * @param latitude The latitude associated with the record.
     * @param longitude The longitude associated with the record.
     */
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
