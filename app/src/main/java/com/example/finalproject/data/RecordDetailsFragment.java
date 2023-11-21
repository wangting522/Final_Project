package com.example.finalproject.data;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.finalproject.MyRecord;
import com.example.finalproject.databinding.DetailsLayoutBinding;

public class RecordDetailsFragment extends Fragment {
MyRecord selected;
    public RecordDetailsFragment(MyRecord m){
        selected = m;
    }
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DetailsLayoutBinding binding = DetailsLayoutBinding.inflate(inflater);

        binding.messageText.setText(selected.getRecord());
        binding.timeText.setText(selected.getTimeSave());
        binding.latitudeText.setText("sunrise time: " + selected.getLatitude());
        binding.longitudeText.setText("sunset time: " + selected.getLongitude());
        return binding.getRoot();
    }
}
