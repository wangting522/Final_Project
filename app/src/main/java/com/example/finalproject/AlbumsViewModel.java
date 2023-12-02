package com.example.finalproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class AlbumsViewModel extends ViewModel {
    public MutableLiveData<ArrayList<DeezerAlbum>> deezerAlbum = new MutableLiveData<>();

    public MutableLiveData<DeezerAlbum> selectedAlbums = new MutableLiveData<>();

}
