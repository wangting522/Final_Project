package com.example.finalproject.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SearchViewModel extends ViewModel {
    public MutableLiveData<ArrayList<SearchWord>> messages = new MutableLiveData< >(null);
    public MutableLiveData<SearchWord> selectedMessage = new MutableLiveData< >();

}

