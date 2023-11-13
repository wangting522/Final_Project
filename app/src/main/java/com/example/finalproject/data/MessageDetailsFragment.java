package com.example.finalproject.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.myapplication.ChatMessage;
import com.example.myapplication.databinding.DetailsLayoutBinding;

public class MessageDetailsFragment extends Fragment {
ChatMessage selected;
    public MessageDetailsFragment(ChatMessage m){
        selected = m;
    }
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DetailsLayoutBinding binding = DetailsLayoutBinding.inflate(inflater);

        binding.messageText.setText(selected.getMessage());
        binding.timeText.setText(selected.getTimeSent());
        binding.databaseText.setText("ID="+ selected.getId());
        return binding.getRoot();
    }
}
