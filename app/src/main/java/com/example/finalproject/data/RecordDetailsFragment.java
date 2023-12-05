package com.example.finalproject.data;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.example.finalproject.databinding.DetailsLayoutBinding;

/**
 * A Fragment class for displaying the details of a selected MyRecord.
 * This fragment is used to show specific details such as record, time, latitude, and longitude
 * of a MyRecord instance.
 */
public class RecordDetailsFragment extends Fragment {

    private final MyRecord selected;

    /**
     * Constructor for RecordDetailsFragment.
     *
     * @param m The MyRecord object whose details are to be displayed.
     */
    public RecordDetailsFragment(MyRecord m) {
        selected = m;
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DetailsLayoutBinding binding = DetailsLayoutBinding.inflate(inflater);

        // Set the text for each view with the appropriate record details
        binding.messageText.setText(selected.getRecord());
        binding.timeText.setText(selected.getTimeSave());
        binding.latitudeText.setText("sunrise time: " + selected.getLatitude());
        binding.longitudeText.setText("sunset time: " + selected.getLongitude());
        return binding.getRoot();
    }
}

