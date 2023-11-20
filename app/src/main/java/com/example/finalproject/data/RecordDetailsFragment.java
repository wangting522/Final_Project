package com.example.finalproject.data;

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
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DetailsLayoutBinding binding = DetailsLayoutBinding.inflate(inflater);

        binding.messageText.setText(selected.getRecord());
        binding.timeText.setText(selected.getTimeSave());
        binding.databaseText.setText("ID="+ selected.getId());
        return binding.getRoot();
    }
}
