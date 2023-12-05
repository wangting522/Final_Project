package com.example.finalproject.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;


/**
 * The AlbumsViewModel class serves as the ViewModel for managing data related to Deezer albums.
 * It holds MutableLiveData objects to observe changes in the list of Deezer albums and the
 * currently selected album.
 */
public class AlbumsViewModel extends ViewModel {
    /**
     * MutableLiveData for the list of Deezer albums. Observers can be notified of changes
     * in the list of albums.
     */
    public MutableLiveData<ArrayList<DeezerAlbum>> deezerAlbum = new MutableLiveData<>();

    /**
     * MutableLiveData for the currently selected Deezer album. Observers can be notified when
     * a new album is selected.
     */
    public MutableLiveData<DeezerAlbum> selectedAlbums = new MutableLiveData<>();
}
