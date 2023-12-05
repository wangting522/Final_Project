package com.example.finalproject.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class FavoritesViewModel extends ViewModel {


    public MutableLiveData<ArrayList<MyRecord>> record = new MutableLiveData<>(new ArrayList<>());


    public MutableLiveData<MyRecord> selectedRecord = new MutableLiveData<>();
}

