package com.example.finalproject.dictionary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalproject.databinding.DetailsLayoutDicBinding;


public class SearchDetailsFragment extends Fragment {

    SearchWord word;

    public SearchDetailsFragment(SearchWord thisWord){
        word = thisWord;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater,container,savedInstanceState);
        DetailsLayoutDicBinding binding = DetailsLayoutDicBinding.inflate(inflater);

        binding.messageId.setText(word.word);
        binding.definitionId.setText(word.definition);
        binding.databaseId.setText(Long.toString(word.id));
        return binding.getRoot();
    }
}

