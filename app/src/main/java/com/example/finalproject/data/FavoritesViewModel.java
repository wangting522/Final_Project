package com.example.finalproject.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalproject.MyRecord;

import java.util.ArrayList;

public class FavoritesViewModel extends ViewModel {
    //public MutableLiveData<ArrayList<FavoritesActivity.MyRecord>> record = new MutableLiveData< >(null);
    public MutableLiveData<ArrayList<MyRecord>> record = new MutableLiveData<>(new ArrayList<>());
    public MutableLiveData<MyRecord> selectedRecord = new MutableLiveData< >();
}
