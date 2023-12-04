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

    @ColumnInfo(name="DefinitionColumn")
    protected String definition;

    public  SearchWord(){}

    public SearchWord(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
