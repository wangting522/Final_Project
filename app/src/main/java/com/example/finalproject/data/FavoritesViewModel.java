package com.example.finalproject.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.finalproject.MyRecord;
import java.util.ArrayList;

/**
 * ViewModel for managing the UI-related data in the lifecycle-conscious way for favorites.
 * This ViewModel is used to hold and manage UI-related data for the FavoritesActivity.
 * It allows data to survive configuration changes such as screen rotations.
 */
public class FavoritesViewModel extends ViewModel {

    /**
     * A LiveData holding a list of MyRecord objects representing the favorite records.
     * MutableLiveData allows the data to be changed and observed for any changes.
     */
    public MutableLiveData<ArrayList<MyRecord>> record = new MutableLiveData<>(new ArrayList<>());

    /**
     * A LiveData for tracking and responding to the selection of an individual record.
     * MutableLiveData allows the selected record to be changed and observed.
     */
    public MutableLiveData<MyRecord> selectedRecord = new MutableLiveData<>();
}

