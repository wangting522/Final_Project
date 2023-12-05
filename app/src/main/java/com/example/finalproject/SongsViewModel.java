package com.example.finalproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * ViewModel class responsible for managing and providing access to song-related data.
 */
public class SongsViewModel extends ViewModel {

    /**
     * MutableLiveData containing the list of songs.
     */
    public MutableLiveData<ArrayList<Songs>> songs = new MutableLiveData<>();

    /**
     * MutableLiveData containing the currently selected song.
     */
    public MutableLiveData<Songs> selectedSongs = new MutableLiveData<>();

    /**
     * Sets the currently selected song in the ViewModel.
     *
     * @param selectedSong The song to set as the selected song.
     */
    public void setSelectedSongs(Songs selectedSong) {
        selectedSongs.setValue(selectedSong);
    }
}