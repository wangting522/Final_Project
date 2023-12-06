package com.example.finalproject.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
/**
 * ViewModel class for handling favorite records and selected record.
 */
public class FavoritesViewModel extends ViewModel {

    /**
     * LiveData holding a list of MyRecord objects representing favorite records.
     */
    public MutableLiveData<ArrayList<MyRecord>> record = new MutableLiveData<>(new ArrayList<>());

    /**
     * LiveData holding the currently selected MyRecord.
     */
    public MutableLiveData<MyRecord> selectedRecord = new MutableLiveData<>();
}

