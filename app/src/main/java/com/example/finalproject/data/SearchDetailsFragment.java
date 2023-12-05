package com.example.finalproject.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalproject.databinding.DetailsLayoutDicBinding;

/**
 * A {@link Fragment} subclass that displays the details of a {@link SearchWord}.
 * This fragment shows the word, its definition, and its database ID.
 */
public class SearchDetailsFragment extends Fragment {

    SearchWord word;

    /**
     * Constructor for creating a fragment with a specific {@link SearchWord}.
     *
     * @param thisWord The {@link SearchWord} whose details are to be shown.
     */
    public SearchDetailsFragment(SearchWord thisWord){
        word = thisWord;
    }
    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     * This method is called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater,container,savedInstanceState);
        DetailsLayoutDicBinding binding = DetailsLayoutDicBinding.inflate(inflater);

        binding.messageId.setText(word.word);
        binding.definitionId.setText(word.definition);
//        binding.databaseId.setText(Long.toString(word.id));
        return binding.getRoot();
    }
}

